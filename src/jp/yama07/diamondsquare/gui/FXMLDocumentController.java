package jp.yama07.diamondsquare.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import jp.yama07.diamondsquare.DiamondSquare;

/**
 * Diamond-Squareアルゴリズムの描画画面のコントローラクラス
 *
 * @author yamamoto
 */
public class FXMLDocumentController implements Initializable {

    private static final int MATRIX_SIZE = 513;
    private static final double MATRIX_VALUE_MIN = 0.0;
    private static final double MATRIX_VALUE_MAX = 1.0;
    // 描画するドットのサイズ（画像の荒さ）
    private static final double SQUARE_ROUGHNESS = 1.0;

    private static final double DEFAULT_HIGHT_ROUGHNESS = 0.1;

    @FXML
    private TextField tfHightRoughness;
    @FXML
    private Canvas cResult;
    @FXML
    private Button bDraw;
    @FXML
    private ProgressIndicator piCalc;

    /**
     * 描画ボタンのハンドル<BR>
     * 値のバラつき割合をTextFieldから読み取り描画を行う。 TextFieldの値が不正の場合は、デフォルト値を使用する。
     *
     * @param event
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {
        double roughness = DEFAULT_HIGHT_ROUGHNESS;
        try {
            roughness = Double.parseDouble(tfHightRoughness.getText());
        } catch (NumberFormatException e) {
            tfHightRoughness.setText(String.valueOf(DEFAULT_HIGHT_ROUGHNESS));
        }

        Task<double[][]> task = new DrawTask(roughness);
        Thread th = new Thread(task);
        //th.setDaemon(true);
        setDisable(true);
        th.start();
    }

    /**
     * 初期化メソッド<BR>
     * TextFieldにデフォルト値をセットし、Canvasを白色に塗りつぶす。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tfHightRoughness.setText(String.valueOf(DEFAULT_HIGHT_ROUGHNESS));
        GraphicsContext gc = cResult.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, cResult.getWidth(), cResult.getHeight());
    }

    /**
     * 画面内のコンポーネンとの有効／無効を切り替える
     *
     * @param value true:無効、 false:有効
     */
    private void setDisable(boolean value) {
        bDraw.setDisable(value);
        tfHightRoughness.setDisable(value);
        piCalc.setVisible(value);
    }

    /**
     * DiamondSqareアルゴリズムでマトリックスを生成しCanvasに描画するタスク
     */
    private class DrawTask extends Task<double[][]> {

        private final double roughness;

        public DrawTask(double roughness) {
            this.roughness = roughness;
        }

        @Override
        protected double[][] call() {
            DiamondSquare ds = new DiamondSquare(MATRIX_SIZE, MATRIX_VALUE_MAX, MATRIX_VALUE_MIN, roughness);
            double[][] matrix = ds.generateMatrix();
            return matrix;
        }

        @Override
        protected void succeeded() {
            draw(getValue());
            setDisable(false);
        }

        /**
         * Canvasにマトリックスを描画する
         */
        private void draw(double[][] matrix) {
            GraphicsContext gc = cResult.getGraphicsContext2D();
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    double value = matrix[i][j];
                    Color color = new Color(value, value, value, 1.0);
                    gc.setFill(color);
                    gc.fillRect(i * SQUARE_ROUGHNESS, j * SQUARE_ROUGHNESS, SQUARE_ROUGHNESS, SQUARE_ROUGHNESS);
                }
            }
        }
    }
}
