package hackattic


sealed interface Task

enum class Secret: Task { Secret00, Secret01 }

enum class Challenge: Task {
    HelpMeUnpack, TalesOfSSL, BruteForceZip, AGlobalPresence, VisualBasicMath, BackupRestore, MiniMiner,
    WebsocketChitChat, ReadingQr, TouchToneDialing, CollisionCourse, BasicFaceDetection, JottingJwts,
    DockerizedSolutions, PasswordHashing;

    val snakeCaseName: String get() = name.toSnakeCase()

    private fun String.toSnakeCase() = replace(Regex("(?<=.)([A-Z])"), "_$1").lowercase()
}


