package jp.yama07.diamondsquare;

/**
 * Diamond-Squareアルゴリズムの実装クラス
 *
 * @author yamamoto
 */
public class DiamondSquare {

    /**
     * デフォルトの最大値
     */
    public static final double DEFAULT_MAX_VALUE = 1.0;
    /**
     * デフォルトの最小値
     */
    public static final double DEFAULT_MIN_VALUE = 0.0;
    /**
     * デフォルトの値のバラつき割合
     */
    public static final double DEFAULT_ROUGHNESS = 0.2;

    private int size;
    private double max;
    private double min;
    private double roughness;
    private double[][] matrix;
    // 一時的に(max-min)*roughnessの値を保持する変数
    private double _roughness_value;

    /**
     * マトリックスのサイズと値の最大値・最小値・バラつき割合を指定<br>
     * マトリックス中の値は、(最大値-最小値)×(バラつき割合)の値に±0.0~1.0の乱数を掛けて算出される値がバラつきとして加えられる。
     *
     * @param size マトリックスのサイズ。size=2^n+1の値を指定する。
     * @param max 値の最大値
     * @param min 値の最小値
     * @param roughness 値のバラつき割合
     */
    public DiamondSquare(int size, double max, double min, double roughness) {
        this.size = size;
        this.max = max;
        this.min = min;
        this.roughness = roughness;
    }

    /**
     * マトリックスのサイズと値の最大値・最小値を指定。バラつき割合はデフォルト値を使用。
     *
     * @param size マトリックスのサイズ。size=2^n+1の値を指定する。
     * @param max 値の最大値
     * @param min 値の最小値
     */
    public DiamondSquare(int size, double max, double min) {
        this(size, max, min, DEFAULT_ROUGHNESS);
    }

    /**
     * マトリックスのサイズを指定。値の最大値・最小値・バラつき割合はデフォルト値を使用。
     *
     * @param size マトリックスのサイズ。size=2^n+1の値を指定する。
     */
    public DiamondSquare(int size) {
        this(size, DEFAULT_MAX_VALUE, DEFAULT_MIN_VALUE);
    }

    public int getSize() {
        return size;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getRoughness() {
        return roughness;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }

    /**
     * Diamond-Squareアルゴリズムで値を埋めたsize×sizeのマトリックスを返す。
     *
     * @return Diamond-Squareアルゴリズムで値を埋めたマトリックス
     */
    public double[][] generateMatrix() {
        calc();
        return matrix;
    }

    /**
     * Diamond-Squareアルゴリズムを実行する。
     */
    private void calc() {
        init();
        for (int half = (size - 1) / 2; 0 < half; half /= 2) {
            // Square
            for (int x = half; x < size; x += half * 2) {
                for (int y = half; y < size; y += half * 2) {
                    square(x, y, half);
                }
            }
            // Diamond
            boolean isEven = true;
            for (int x = 0; x < size; x += half) {
                for (int y = isEven ? half : 0; y < size; y += half * 2) {
                    diamond(x, y, half);
                }
                isEven = !isEven;
            }
        }
    }
        
    /**
     * Diamond-Squareアルゴリズムの初期化<br>
     * マトリックスのインスタンス生成と四隅の値の代入を行う。
     */
    private void init() {
        matrix = new double[size][size];
        _roughness_value = (max - min) * roughness;
        double halfValue = (max - min) / 2;
        double[] values = new double[4];
        for (int i = 0; i < values.length; i++) {
            double value = halfValue + ((1.0 - 2 * Math.random()) * _roughness_value);
            if (value < min) {
                value = min;
            }
            if (max < value) {
                value = max;
            }
            values[i] = value;
        }
        matrix[0][0] = values[0];
        matrix[0][size - 1] = values[1];
        matrix[size - 1][0] = values[2];
        matrix[size - 1][size - 1] = values[3];
    }

    /**
     * ダイアモンド型のサブマトリックスの四隅平均値の代入を行う。
     *
     * @param x マトリックスのxインデックス
     * @param y マトリックスのyインデックス
     * @param half サブマトリックスのサイズ
     */
    private void diamond(int x, int y, int half) {
        double sum = 0;
        int count = 0;
        try {
            sum += matrix[x - half][y];
            count++;
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sum += matrix[x][y - half];
            count++;
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sum += matrix[x][y + half];
            count++;
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sum += matrix[x + half][y];
            count++;
        } catch (IndexOutOfBoundsException e) {
        }

        double value = sum / count + ((1.0 - 2 * Math.random()) * _roughness_value);
        if (value < min) {
            value = min;
        }
        if (max < value) {
            value = max;
        }
        matrix[x][y] = value;
    }

    /**
     * スクウェア型のサブマトリックスの四隅平均値の代入を行う。
     *
     * @param x マトリックスのxインデックス
     * @param y マトリックスのyインデックス
     * @param half サブマトリックスのサイズ
     */
    private void square(int x, int y, int half) {
        double value = (matrix[x - half][y - half]
                + matrix[x - half][y + half]
                + matrix[x + half][y - half]
                + matrix[x + half][y + half]) / 4 + ((1.0 - 2 * Math.random()) * _roughness_value);
        if (value < min) {
            value = min;
        }
        if (max < value) {
            value = max;
        }
        matrix[x][y] = value;
    }

}
