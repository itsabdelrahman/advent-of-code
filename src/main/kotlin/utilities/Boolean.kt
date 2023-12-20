package utilities

fun <T> Boolean.andThen(block: () -> T): T? = if (this) {
    block()
} else {
    null
}
