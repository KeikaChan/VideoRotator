package work.airz

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File


fun main(args: Array<String>) {
    if (args.size < 2) {
        println("usage")
        println("java -jar /source/path/mp4 ...  /dest/path/")
        System.exit(1)
    }
    for (index in 0 until args.size - 1) {
        if (File(args[index]).isDirectory || !File(args[index]).exists()) {
            println("入力が不正")
            System.exit(1)
        }
    }
    if (!File(args.last()).isDirectory) {
        println("出力先が不正")
        System.exit(1)
    }
    for (index in 0 until args.size - 1) {
        encodeFile(File(args[index]), File(args.last()))
    }

}

fun encodeFile(input: File, dest: File) {
    val currentDir = System.getProperty("user.dir")
    val ffmpegExec = File(currentDir + File.separator + "encoder", getExtByPlatform("ffmpeg"))
    val ffprobeExec = File(currentDir + File.separator + "encoder", getExtByPlatform("ffprobe"))
    if (!ffmpegExec.exists() || !ffprobeExec.exists()) {
        println("encoder does not exist!")
    }

    val ffmpeg = FFmpeg(ffmpegExec.absolutePath)
    val ffmprobe = FFprobe(ffprobeExec.absolutePath)

    /**
     * formatについて
     * streams[0]がビデオ,streams[1]がオーディオ
     */
    val format = ffmprobe.probe(input.absolutePath)
    val videoFormat = format.streams[0]
    val audioFormat = format.streams[1]

//    //now encoding
    val ffmpegBuilder = FFmpegBuilder()
            .setInput(input.absolutePath)
            .overrideOutputFiles(true)
            .addOutput(File(dest.absolutePath, "${input.nameWithoutExtension}-rotate.${input.extension}").absolutePath)
            .setAudioChannels(audioFormat.channels)
            .setAudioCodec(audioFormat.codec_name)        // using the aac codec
            .setAudioSampleRate(audioFormat.sample_rate)  // at 48KHz
            .setAudioBitRate(audioFormat.bit_rate)      // at 32 kbit/s

            .setVideoCodec("libx264")     // Video using x264
            .setVideoFrameRate(videoFormat.r_frame_rate.numerator, videoFormat.r_frame_rate.denominator)     // at 24 frames per second
            .setVideoBitRate(videoFormat.bit_rate)
            .setVideoResolution(1080, 1920) // at 640x480 resolution
            .setFormat("mp4")
            .addExtraArgs("-vf", "transpose=2")
//            .setVideoQuality(16.0)
            .done()

    val executor = FFmpegExecutor(ffmpeg, ffmprobe)
    val job = executor.createTwoPassJob(ffmpegBuilder)
    println("start encoding")
    job.run()
    println("finish")
}
