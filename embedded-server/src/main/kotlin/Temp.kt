import kotlinx.coroutines.delay

suspend fun main() {
    for (c in "Hello") {
        println(c)
        delay(300)
    }
}