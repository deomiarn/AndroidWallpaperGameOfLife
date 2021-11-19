package ch.openscript.gameoflifepaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

public class GameOfLifeWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("storage", 0);
        int backgroundColor = pref.getInt("backgroundColor", Color.GRAY);
        int cellColor = pref.getInt("cellColor", Color.BLACK);

        return new GameOfLifeWallpaperEngine(backgroundColor, cellColor);
    }

    private class GameOfLifeWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = () -> draw();
        private boolean touchEnabled;
        private List<RectF> squares;
        private Paint paint = new Paint();
        private int width;
        private int height;
        private boolean visible = true;

        private Canvas canvas = null;
        private int backgroundColor;
        private int differenceCellColor = 80;
        private int defaultCellColor;
        private int counter = 0;
        private int numColumns = 40;
        private int numRows = 80;
        private int delay = 50;
        private int[][] cellChecked = new int[numColumns][numRows];
        private int cellWidth, cellHeight;
        private Paint cellPaint = new Paint();

        public GameOfLifeWallpaperEngine(int backgroundColor, int cellColor) {
            this.backgroundColor = backgroundColor;
            this.defaultCellColor = cellColor;
            touchEnabled = true;
            squares = new ArrayList<>();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(50);
            handler.post(drawRunner);
            cellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
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
                createSquares(80);
            }

            if (squares.size() < 200) {
                createSquares(40);
            }

            calculateDimensions();
            SurfaceHolder holder = getSurfaceHolder();

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {

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

        private void createSquares(int generatedCells) {
            for (int i = 0; i < generatedCells; i++) {
                boolean onDuplicate = true;

                while (onDuplicate) {
                    int column = (int) (Math.random() * numColumns);
                    int row = (int) (Math.random() * numRows);

                    if (column < numColumns - 1 && column > 1 && row < numRows - 1 && row > 1) {
                        if (cellChecked[column][row] != 1) {
                            onDuplicate = false;
                        }

                        RectF square1 = new RectF(column, row, column, row);
                        RectF square2 = new RectF(column + 1, row - 1, column + 1, row - 1);
                        RectF square3 = new RectF(column, row - 1, column, row - 1);
                        RectF square4 = new RectF(column - 1, row - 1, column - 1, row - 1);

                        if (i > 0) {
                            if (3 % i == 0) {
                                square2 = new RectF(column + 1, row, column + 1, row);
                                square3 = new RectF(column - 1, row - 1, column - 1, row - 1);
                                square4 = new RectF(column - 1, row, column - 1, row);
                            }
                        }
                        squares.add(square1);
                        squares.add(square2);
                        squares.add(square3);
                        squares.add(square4);

                        cellChecked[(int) square1.right][(int) square1.top] = 1;
                        cellChecked[(int) square2.right][(int) square2.top] = 1;
                        cellChecked[(int) square3.right][(int) square3.top] = 1;
                        cellChecked[(int) square4.right][(int) square4.top] = 1;
                    }
                }
            }
        }

        private void drawSquares(Canvas canvas) {
            int color = defaultCellColor;
            int color2 = color - differenceCellColor;

            if (color <= -16777126) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.RED && color >= Color.RED - 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.GRAY && color >= Color.GRAY - 100) {
                color2 = color + differenceCellColor;
            } else if (color >= Color.BLACK + 100 && color <= Color.BLACK + 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.DKGRAY && color >= Color.DKGRAY - 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.WHITE && color >= Color.WHITE - 100) {
                color2 = color - differenceCellColor;
            } else if (color <= Color.GREEN && color >= Color.GREEN - 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.BLUE && color >= Color.BLUE - 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.YELLOW && color >= Color.YELLOW - 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.CYAN && color >= Color.CYAN - 100) {
                color2 = color + differenceCellColor;
            } else if (color <= Color.MAGENTA && color >= Color.MAGENTA - 100) {
                color2 = color + differenceCellColor;
            }

            int counter = 0;
            for (int i = 0; i < numColumns; i++) {
                for (int j = 0; j < numRows; j++) {
                    if (cellChecked[i][j] == 1) {
                        counter = counter + 1;
                        if (counter > 3) {
                            counter = 0;
                        }
                        if (counter == 1) {
                            cellPaint.setColor(color);
                        } else if (counter == 2) {
                            cellPaint.setColor(color2);
                        }

                        canvas.drawRect(i * cellWidth, j * cellHeight,
                                (i + 1) * cellWidth, (j + 1) * cellHeight,
                                cellPaint);
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

            for (int l = 1; l < columns - 1; l++) {
                for (int m = 1; m < rows - 1; m++) {

                    int aliveNeighbours = 0;
                    for (int i = -1; i <= 1; i++)
                        for (int j = -1; j <= 1; j++)
                            aliveNeighbours += grid[l + i][m + j];
                    aliveNeighbours -= grid[l][m];

                    if ((grid[l][m] == 1) && (aliveNeighbours < 2))
                        future[l][m] = 0;

                    else if ((grid[l][m] == 1) && (aliveNeighbours > 3))
                        future[l][m] = 0;

                    else if ((grid[l][m] == 0) && (aliveNeighbours == 3))
                        future[l][m] = 1;

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

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (touchEnabled) {
                counter = counter + 1;
                float x = event.getX();
                float y = event.getY();

                int column = (int) Math.floor(x / cellWidth);

                if (column < 2) {
                    column = 2;
                } else if (column >= numColumns - 1) {
                    column = numColumns - 3;
                }

                int row = (int) Math.floor(y / cellHeight);

                if (row < 2) {
                    row = 2;
                }

                if (row >= numRows - 1) {
                    row = numRows - 3;
                }
                SurfaceHolder holder = getSurfaceHolder();

                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        RectF square1 = new RectF(column, row, column, row);
                        RectF square2 = new RectF(column + 1, row - 1, column + 1, row - 1);
                        RectF square3 = new RectF(column, row - 1, column, row - 1);
                        RectF square4 = new RectF(column - 1, row - 1, column - 1, row - 1);

                        if (counter % 3 == 0) {
                            square2 = new RectF(column + 1, row, column + 1, row);
                            square3 = new RectF(column - 1, row - 1, column - 1, row - 1);
                            square4 = new RectF(column - 1, row, column - 1, row);
                        }

                        squares.add(square1);
                        squares.add(square2);
                        squares.add(square3);
                        squares.add(square4);

                        cellChecked[(int) square1.right][(int) square1.top] = 1;
                        cellChecked[(int) square2.right][(int) square2.top] = 1;
                        cellChecked[(int) square3.right][(int) square3.top] = 1;
                        cellChecked[(int) square4.right][(int) square4.top] = 1;

                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
                super.onTouchEvent(event);

                handler.removeCallbacks(drawRunner);
                if (visible) {
                    handler.postDelayed(drawRunner, delay);
                }
            }
        }
    }
}