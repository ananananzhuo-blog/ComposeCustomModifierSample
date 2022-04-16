package com.ananananzhuo.composecustommodifiersample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.ananananzhuo.composecustommodifiersample.ui.theme.ComposeCustomModifierSampleTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCustomModifierSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    var checked by remember {
        mutableStateOf(false)
    }
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Box(Modifier
            .size(200.dp, 200.dp)
            .customDraw(color = Color.Green, starCount = 5, checked = checked)
            .clickable {
                checked = !checked
            }
        ) {

        }
    }

}

fun Modifier.customDraw(
    color: Color,
    starCount: Int = 5,
    checked: Boolean = false,
) =
    this.then(CustomDrawModifier(color, starCount, checked = checked))

class CustomDrawModifier(
    private val color: Color,
    private val starCount: Int = 5,//星的数量
    private var checked: Boolean = false,
) :
    DrawModifier {
    override fun ContentDrawScope.draw() {
        log("$size")
        val radiusOuter = if (size.width > size.height) size.height / 2 else size.width / 2 //五角星外圆径
        val radiusInner = radiusOuter / 2 //五角星内圆半径
        val startAngle = (-Math.PI / 2).toFloat() //开始绘制点的外径角度
        val perAngle = (2 * Math.PI / starCount).toFloat() //两个五角星两个角直接的角度差
        val outAngles = (0 until starCount).map {
            val angle = it * perAngle + startAngle
            Offset(radiusOuter * cos(angle), radiusOuter * sin(angle))
        }//所有外圆角的顶点
        val innerAngles = (0 until starCount).map {
            val angle = it * perAngle + perAngle / 2 + startAngle
            Offset(radiusInner * cos(angle), radiusInner * sin(angle))
        }//所有内圆角的顶点
        val path = Path()//绘制五角星的所有内圆外圆的点连接线
        (0 until starCount).forEachIndexed { index, _ ->
            val outerX = outAngles[index].x
            val outerY = outAngles[index].y
            val innerX = innerAngles[index].x
            val innerY = innerAngles[index].y
//            drawCircle(Color.Red, radius = 3f, center = outAngles[index])
//            drawCircle(Color.Yellow, radius = 3f, center = innerAngles[index])
            if (index == 0) {
                path.moveTo(outerX, outerY)
                path.lineTo(innerX, innerY)
                path.lineTo(outAngles[(index + 1) % starCount].x,
                    outAngles[(index + 1) % starCount].y)
            } else {
                path.lineTo(innerX, innerY)//移动到内圆角的端点
                path.lineTo(outAngles[(index + 1) % starCount].x,
                    outAngles[(index + 1) % starCount].y)//连接到下一个外圆角的端点
            }
            if (index == starCount - 1) {
                path.close()
            }
        }
        translate(size.width / 2, size.height / 2) {
            drawPath(path, color, style = if (checked) Fill else Stroke(width = 5f))
        }
    }

}

class CustomStarModifier : LayoutModifier {
    init {
        then(Modifier.drawWithContent {
            drawCircle(Color.Green, radius = 100f, center = Offset(100f, 100f))
        })
    }

    private val wrapConstraints = Constraints(
        minWidth = 200,
        minHeight = 200,
        maxWidth = 400,
        maxHeight = 400
    )

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(wrapConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}


fun log(msg: String) {
    Log.e("日志", msg)
}