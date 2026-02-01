package hackattic

sealed interface Task

enum class Secret: Task { Secret00, Secret01 }

enum class Challenge: Task {
    HelpMeUnpack, TalesOfSSL, BruteForceZip, AGlobalPresence, VisualBasicMath, BackupRestore, MiniMiner,
    WebsocketChitChat, ReadingQr
}
