package work.airz

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File

val ffmpegExec = File("/usr/local/bin/ffmpeg")
val ffprobeExec = File("/usr/local/bin/ffprobe")

fun encodeFile(input: File, destFolder: File, height: Int, width: Int, videorate: Long, ratenum: Int, ratedeno: Int, audiorate: Long) {
    val currentDir = System.getProperty("user.dir")
//    val ffmpegExec = File(currentDir + File.separator + "encoder", getExtByPlatform("ffmpeg"))
//    val ffprobeExec = File(currentDir + File.separator + "encoder", getExtByPlatform("ffprobe"))

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
            .addOutput(File(destFolder.absolutePath, "${input.nameWithoutExtension}-rotate.${input.extension}").absolutePath)
            .setAudioChannels(audioFormat.channels)
            .setAudioCodec(audioFormat.codec_name)        // using the aac codec
            .setAudioSampleRate(audioFormat.sample_rate)  // at 48KHz
            .setAudioBitRate(audiorate)      // at 32 kbit/s
            .setPreset("slow")
            .setVideoCodec("hevc")     // Video using x264
            .setConstantRateFactor(18.0) //h.265用
            .setVideoFrameRate(ratenum, ratedeno)
            .setVideoBitRate(videorate)
            .setVideoResolution(width, height) // at 640x480 resolution
            .setFormat("mp4")
            .addExtraArgs("-vf", "transpose=2", "-c:v", "hevc_videotoolbox", "-tag:v", "hvc1") //only work Macbooks
//            .setVideoQuality(16.0)
            .done()

    val executor = FFmpegExecutor(ffmpeg, ffmprobe)
    val job = executor.createTwoPassJob(ffmpegBuilder)
    println("start encoding")
    job.run()
    println("finish")
}
