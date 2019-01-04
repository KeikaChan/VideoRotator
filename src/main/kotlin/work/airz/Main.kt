package work.airz

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File


fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App : Application() {
    companion object {
        const val WINDOW_HEIGHT = 540.0
        const val WINDOW_WIDTH = 700.0

    }

    override fun start(primaryStage: Stage) {

        val fxmlLoader = FXMLLoader(javaClass.getResource("/main.fxml"))
        val root = fxmlLoader.load<Parent>()
        primaryStage.title = "動画変換"
        primaryStage.scene = Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT)
        primaryStage.minHeight = WINDOW_HEIGHT
        primaryStage.minWidth = WINDOW_WIDTH
        primaryStage.maxHeight = WINDOW_HEIGHT
        primaryStage.maxWidth = WINDOW_WIDTH
        primaryStage.show()

    }

}
