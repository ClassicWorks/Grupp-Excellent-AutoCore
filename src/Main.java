
import com.wac.autocore.manager.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ViewManager.getInstance().init(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}