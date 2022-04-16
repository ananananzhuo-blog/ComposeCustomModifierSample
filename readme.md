
## 说明
compose中我们的所有ui操作，包括一些行为，例如：点击、手势等都需要使用Modifier来进行操作。由此对Modifier的理解可以帮助我们解决很多问题的

## 自定义星行Modifier
本文我们打算自定义一个Modifier，通过这个modifier我们可以实现用一个操作符就画出五角星的效果

### 原理
我们实现绘制五角星的原理如下图，首先我们会虚构两个圆，将内圆和外圆角度平分五份，然后依次连接内圆和外圆的切点的坐标，使用path即可绘制完成。
![](https://files.mdnice.com/user/15648/3195ccff-416f-46bf-9100-fead8c1300f4.png)

### 实现
代码中的实现涉及到自定义绘制，难度并不大。需要注意的点：
1. composse中角度的锚点是弧度（Math.PI）、而原生的锚点是角度(360)
2. 默认的原点在左上角，我们绘制的时候需要主动移动到组合的中心点
3. path的绘制使用Fill可以填充闭合路径图形，使用Stroke可以绘制线性闭合路径图形


### 代码
```kt

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

```

## 最终实现效果

![](https://files.mdnice.com/user/15648/aaec1fc6-86ed-4774-b18a-1ccbb2ff4857.gif)

