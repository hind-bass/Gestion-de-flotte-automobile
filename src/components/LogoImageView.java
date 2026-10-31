package components;

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LogoImageView extends ImageView {

    private static final String DEFAULT_LOGO_PATH = "/images/logo.png";
   
    private static final double DEFAULT_SIZE = 50.0;

    public LogoImageView() {
        this(DEFAULT_LOGO_PATH, DEFAULT_SIZE);
    }
    public LogoImageView(String imagePath, double size) {
        super();
        configureImageView(imagePath, size);
        setupHoverEffects();
        applyCircularClip();
    }

    private void configureImageView(String imagePath, double size) {
        try {
            Image logoImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(imagePath)));
            this.setImage(logoImage);
        } catch (NullPointerException | IllegalArgumentException e) {
            this.setImage(createDefaultLogo());
        }

        this.setFitWidth(size);
        this.setFitHeight(size);
        this.setPreserveRatio(true);
        this.setSmooth(true);
        this.setCache(true);
    }

    private Image createDefaultLogo() {
        return new Image(Objects.requireNonNull(
            getClass().getResourceAsStream(DEFAULT_LOGO_PATH)));
    }

    private void setupHoverEffects() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), this);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), this);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        this.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });

        this.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });

        this.setOnMouseClicked(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(500), this);
            ft.setFromValue(1.0);
            ft.setToValue(0.7);
            ft.setAutoReverse(true);
            ft.setCycleCount(2);
            ft.play();
        });
    }

    private void applyCircularClip() {
        Circle clip = new Circle();
        clip.radiusProperty().bind(this.fitWidthProperty().divide(2));
        clip.centerXProperty().bind(this.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(this.fitHeightProperty().divide(2));
        this.setClip(clip);
    }

    public void pulseLogo() {
        ScaleTransition st = new ScaleTransition(Duration.millis(1000), this);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(Animation.INDEFINITE);
        st.play();
    }

}

