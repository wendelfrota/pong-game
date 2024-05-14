import korlibs.event.*
import korlibs.time.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.tween.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import kotlinx.coroutines.*

suspend fun main() = Korge(windowSize = Size(1280, 720), bgcolor = Colors["#2b2b2b"]) {
    sceneContainer().changeTo({ Game() })
}

class Game : Scene() {
    val radiusBall = 25.0
    val paddleSize = intArrayOf(15,100)

    lateinit var ball: View
    lateinit var paddle1: View
    lateinit var paddle2: View

    val velocity = Point(15.0, 7.5)

    var velocityX = velocity.x
    var velocityY = velocity.y

    override suspend fun SContainer.sceneInit(){
        ball = circle(radiusBall, Colors.FIREBRICK).xy((width/2)-(radiusBall*2),(height/2)-(radiusBall*2))
        paddle1 = solidRect(paddleSize[0], paddleSize[1], Colors.WHITE).xy(20, (height/2)-paddleSize[1])
        paddle2 = solidRect(paddleSize[0], paddleSize[1], Colors.WHITE).xy(width-(paddleSize[0]*2),(height/2)-paddleSize[1])
    }

    override suspend fun SContainer.sceneMain() {
        addUpdater{
            ball.x += velocityX
            ball.y += velocityY
            paddle2.y += velocityY

            stage!!.controlWithKeyboard(paddle1)

            isOffTheMapY(paddle1)
            isOffTheMapY(paddle2)

            if (ball.x < 0) {
                views.launch {
                    sceneContainer.changeTo<GameOver>()
                }
            }

            if (ball.y < 0 || ball.y + ball.height > height)
                velocityY = -velocityY

            if (isCollision(ball, paddle1) || isCollision(ball, paddle2))
                velocityX = -velocityX
        }
    }

    fun Stage.controlWithKeyboard(paddle: View, up: Key = Key.UP, down: Key = Key.DOWN){
        if (keys[up])
            paddle.y -= velocity.y + 2
        else if (keys[down])
            paddle.y += velocity.y + 2
    }

    private fun isOffTheMapY(obj1: View){
        if (obj1.y < 0)
            obj1.y = 0.0
        else if (obj1.y + obj1.height > stage.height)
            obj1.y = stage.height - obj1.height
    }

    private fun isOffTheMapY_Test(obj1: View): Boolean{
        return (obj1.y < 0) || (obj1.y + obj1.height > stage.height)
    }

    private fun isCollision(obj1: View, obj2: View): Boolean{
        return obj1.x < obj2.x + obj2.width &&
            obj1.x + obj1.width > obj2.x &&
            obj1.y < obj2.y + obj2.height &&
            obj1.y + obj1.height > obj2.y
    }
}

class GameOver() : Scene(){
    override suspend fun SContainer.sceneInit() {
        text("Game Over!", textSize = 40.0, Colors.WHITE)
    }

    override suspend fun SContainer.sceneMain() {
        delay(5.seconds)
        sceneContainer.changeTo { Game() }
    }
}
