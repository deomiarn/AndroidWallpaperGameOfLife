package ch.openscript.gameoflifepaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

public class GameOfLifeWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("storage", 0);
        int backgroundColor = pref.getInt("backgroundColor", Color.RED);

        return new GameOfLifeWallpaperEngine(backgroundColor);
    }

    private class GameOfLifeWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = () -> draw();
        private List<RectF> squares;
        private Paint paint = new Paint();
        private int width;
        private int height;
        private boolean visible = true;
        private int maxNumber;

        private int backgroundColor;
        private int numColumns = 40;
        private int numRows = 80;
        private int generatedCells = 750;
        private int delay = 100;
        private int[][] cellChecked = new int[numColumns][numRows];
        private int cellWidth, cellHeight;
        private Paint blackPaint = new Paint();

        public GameOfLifeWallpaperEngine(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            maxNumber = 400;
            squares = new ArrayList<>();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(50);
            handler.post(drawRunner);
            blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        private void calculateDimensions() {
            if (numColumns < 1 || numRows < 1) {
                return;
            }
            cellWidth = width / numColumns;
            cellHeight = height / numRows;
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        private Canvas drawCanvas(Canvas canvas) {
            canvas.drawColor(backgroundColor);

            if (numColumns == 0 || numRows == 0) {
                return null;
            }

            return canvas;
        }

        private void draw() {
            if (squares.size() == 0) {
                createBeginningSquares();
            }

            calculateDimensions();
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    if (squares.size() >= maxNumber) {
                        squares.clear();
                    }

                    canvas = drawCanvas(canvas);
                    nextGeneration();
                    drawSquares(canvas);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, delay);
            }
        }

        private void createBeginningSquares() {
            for (int i = 0; i < generatedCells; i++) {

                boolean onDuplicate = true;
                int row = 0;
                int column = 0;

                while (onDuplicate) {
                    column = (int) (Math.random() * numColumns);
                    row = (int) (Math.random() * numRows);

                    if (cellChecked[column][row] != 1) {
                        onDuplicate = false;
                    }
                }
                RectF square = new RectF(column, row, column, row);

                squares.add(square);
                cellChecked[(int) square.right][(int) square.top] = 1;
            }
        }

        private void drawSquares(Canvas canvas) {
            for (int i = 0; i < numColumns; i++) {
                for (int j = 0; j < numRows; j++) {
                    if (cellChecked[i][j] == 1) {
                        canvas.drawRect(i * cellWidth, j * cellHeight,
                                (i + 1) * cellWidth, (j + 1) * cellHeight,
                                blackPaint);
                    }
                }
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }



        private void nextGeneration() {
            int columns = numColumns;
            int rows = numRows;

            int[][] grid = cellChecked;
            int[][] future = new int[columns][rows];

            // Loop through every cell
            for (int l = 1; l < columns - 1; l++) {
                for (int m = 1; m < rows - 1; m++) {
                    // finding no Of Neighbours that are alive
                    int aliveNeighbours = 0;
                    for (int i = -1; i <= 1; i++)
                        for (int j = -1; j <= 1; j++)
                            aliveNeighbours += grid[l + i][m + j];

                    // The cell needs to be subtracted from
                    // its neighbours as it was counted before
                    aliveNeighbours -= grid[l][m];

                    // Implementing the Rules of Life

                    // Cell is lonely and dies
                    if ((grid[l][m] == 1) && (aliveNeighbours < 2))
                        future[l][m] = 0;

                        // Cell dies due to over population
                    else if ((grid[l][m] == 1) && (aliveNeighbours > 3))
                        future[l][m] = 0;

                        // A new cell is born
                    else if ((grid[l][m] == 0) && (aliveNeighbours == 3))
                        future[l][m] = 1;

                        // Remains the same
                    else
                        future[l][m] = grid[l][m];
                }
            }
            createNewSquares(future);
            cellChecked = future;
        }


        private void createNewSquares(int[][] grid) {
            squares.clear();

            for (int i = 0; i < grid.length; i++)
                for (int j = 0; j < grid[i].length; j++)
                    if (grid[i][j] == 1) {
                        squares.add(new RectF(i, j, i, j));
                    }
        }
    }
}