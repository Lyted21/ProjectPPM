import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape._
import javafx.scene.transform.{Rotate, Translate}
import javafx.scene.{Group, Node}
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.{PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import scala.io.Source

class Main extends Application {

  //Auxiliary types
  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node]) //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))


  override def start(stage: Stage): Unit = {

    //Get and print program arguments (args: Array[String])
    val params = getParameters
    println("Program arguments:" + params.getRaw)

    val redMaterial = new PhongMaterial()
    redMaterial.setDiffuseColor(Color.rgb(150, 0, 0))

    val greenMaterial = new PhongMaterial()
    greenMaterial.setDiffuseColor(Color.rgb(0, 150, 0))

    val blueMaterial = new PhongMaterial()
    blueMaterial.setDiffuseColor(Color.rgb(0, 0, 150))

    val newMaterial = new PhongMaterial()
    newMaterial.setDiffuseColor((Color.rgb(0, 150, 150)))

    //3D objects
    val lineX = new Line(0, 0, 200, 0)
    lineX.setStroke(Color.GREEN)

    val lineY = new Line(0, 0, 0, 200)
    lineY.setStroke(Color.YELLOW)

    val lineZ = new Line(0, 0, 200, 0)
    lineZ.setStroke(Color.LIGHTSALMON)
    lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

    val camVolume = new Cylinder(10, 50, 10)
    camVolume.setTranslateX(1)
    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
    camVolume.setMaterial(blueMaterial)
    camVolume.setDrawMode(DrawMode.LINE)


    //translate because it is added by defaut to the coords (0,0,0)
    val wiredBox = new Box(32, 32, 32)
    wiredBox.setTranslateX(16)
    wiredBox.setTranslateY(16)
    wiredBox.setTranslateZ(16)
    wiredBox.setMaterial(newMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    val box1 = new Box(20, 1, 10) //
    box1.setTranslateX(5)
    box1.setTranslateY(5)
    box1.setTranslateZ(5)
    box1.setMaterial(greenMaterial)
    box1.setDrawMode(DrawMode.FILL)

    def createModels(lst: List[String]): Cylinder = {
      lst match {
        case Nil => new Cylinder()
        case x :: Nil => {
          val y = x.split(" ")
          val colors = y(1).substring(1, y(1).length - 1).split(",")
          val cilindro = new Cylinder(0.5, 1, 10)
          cilindro.setTranslateX(y(2).toDouble)
          cilindro.setTranslateY(y(3).toDouble)
          cilindro.setTranslateZ(y(4).toDouble)
          cilindro.setScaleX(y(5).toDouble)
          cilindro.setScaleY(y(6).toDouble)
          cilindro.setScaleZ(y(7).toDouble)
          val newMaterial = new PhongMaterial()
          newMaterial.setDiffuseColor(Color.rgb(colors(0).toInt, colors(1).toInt, colors(2).toInt))
          cilindro.setMaterial(newMaterial)
          cilindro
        }
      }
    }

    def readFile(file: String): List[String] = {
      val bufferedSource = Source.fromFile(file).getLines().toList
      bufferedSource
    }

    val home = System.getProperty("user.home")

    val cylinder3 = createModels(readFile(s"${home}/Desktop/config.txt"))
    7
    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot: Group = new Group()
    // Camera
    val camera = new PerspectiveCamera(true)

    val cameraTransform = new CameraTransformer
    cameraTransform.setTranslate(0, 0, 0)
    cameraTransform.getChildren.add(camera)
    camera.setNearClip(0.1)
    camera.setFarClip(10000.0)

    camera.setTranslateZ(-500)
    camera.setFieldOfView(20)
    cameraTransform.ry.setAngle(-45.0)
    cameraTransform.rx.setAngle(-45.0)
    worldRoot.getChildren.add(cameraTransform)

    // SubScene - composed by the nodes present in the worldRoot
    val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
    subScene.setFill(Color.DARKSLATEGRAY)
    subScene.setCamera(camera)


    // CameraView - an additional perspective of the environment
    val cameraView = new CameraView(subScene)
    cameraView.setFirstPersonNavigationEabled(true)
    cameraView.setFitWidth(350)
    cameraView.setFitHeight(225)
    cameraView.getRx.setAngle(-45)
    cameraView.getT.setZ(-100)
    cameraView.getT.setY(-500)
    cameraView.getCamera.setTranslateZ(-50)
    cameraView.startViewing

    //Position of the CameraView: Right-bottom corner
    StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
    StackPane.setMargin(cameraView, new Insets(5))

    // Scene - defines what is rendered (in this case the subScene and the cameraView)
    val root = new StackPane(subScene)
    subScene.widthProperty.bind(root.widthProperty)
    subScene.heightProperty.bind(root.heightProperty)

    val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)

    //setup and start the Stage
    stage.setTitle("PPM Project 21/22")
    stage.setScene(scene)
    stage.show

    //oct1 - example of an Octree[Placement] that contains only one Node (i.e. cylinder1)
    //In case of difficulties to implement task T2 this octree can be used as input for tasks T3, T4 and T5

    val b2 = new Box(8, 8, 8)
    //translate because it is added by defaut to the coords (0,0,0)
    b2.setTranslateX(8 / 2)
    b2.setTranslateY(8 / 2)
    b2.setTranslateZ(8 / 2)
    b2.setMaterial(redMaterial)
    b2.setDrawMode(DrawMode.LINE)

    val b3 = new Box(4, 4, 4)
    //translate because it is added by defaut to the coords (0,0,0)
    b3.setTranslateX(4 / 2)
    b3.setTranslateY(4 / 2)
    b3.setTranslateZ(4 / 2)
    b3.setMaterial(newMaterial)
    b3.setDrawMode(DrawMode.LINE)


    val placement1: Placement = ((0, 0, 0), 8.0)
    val sec1: Section = (((0.0, 0.0, 0.0), 4.0), List(b3))
    val sec2: Section = (((0.0, 0.0, 0.0), 8.0), List(b2))
    val ocLeaf1 = OcLeaf(sec1)
    val ocLeaf2 = OcLeaf(sec2)
    val oct1: Octree[Placement] = OcNode[Placement](placement1, ocLeaf1, ocLeaf2, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty)
    //adding boxes b2 and b3 to the world
    worldRoot.getChildren.add(b2)
    worldRoot.getChildren.add(b3)

    def scaleOctree(fact: Double, oc: Octree[Placement]): Unit = {
      oc match {
        case OcEmpty => Nil
        case OcNode(coords, leaf1, leaf2, leaf3, leaf4, leaf5, leaf6, leaf7, leaf8) =>
          scaleOctree(fact, leaf1)
          scaleOctree(fact, leaf2)
          scaleOctree(fact, leaf3)
          scaleOctree(fact, leaf4)
          scaleOctree(fact, leaf5)
          scaleOctree(fact, leaf6)
          scaleOctree(fact, leaf7)
          scaleOctree(fact, leaf8)
        case OcLeaf(section: Section) => val old = section._2.head.asInstanceOf[Box]
          worldRoot.getChildren.remove(old)
        val b = new Box(old.getWidth, old.getHeight, old.getDepth)
        b.setTranslateX(old.getTranslateX)
          b.setTranslateY(old.getTranslateY)
          b.setTranslateZ(old.getTranslateZ)
          b.setDrawMode(old.getDrawMode)
        b.setScaleX(fact)
        b.setScaleY(fact)
        b.setScaleZ(fact)
          b.setMaterial(old.getMaterial)
          worldRoot.getChildren.add(b)
      }
    }

    def greenRemove(c: Color): Color = {
      val c1 = Color.color(c.getRed, 0, c.getBlue)
      c1
    }

    def sepia(c: Color): Color = {
      val c1 = Color.color((0.4*c.getRed + 0.77*c.getGreen + 0.2*c.getBlue), (0.35*c.getRed + 0.69*c.getGreen + 0.17*c.getBlue), (0.27*c.getRed + 0.53*c.getGreen + 0.13*c.getBlue))
      c1
    }

    def mapColourEffect(func: Color => Color, oct: Octree[Placement]): Unit = {
      oct match {
        case OcEmpty => oct
        case OcNode(coords, leaf1, leaf2, leaf3, leaf4, leaf5, leaf6, leaf7, leaf8) =>
          mapColourEffect(func, leaf1)
          mapColourEffect(func, leaf2)
          mapColourEffect(func, leaf3)
          mapColourEffect(func, leaf4)
          mapColourEffect(func, leaf5)
          mapColourEffect(func, leaf6)
          mapColourEffect(func, leaf7)
          mapColourEffect(func, leaf8)
        case OcLeaf(section: Section) => worldRoot.getChildren.remove(section._2.head)
          section._2.map(x => {
            val mt = new PhongMaterial()
            mt.setDiffuseColor(func(x.asInstanceOf[Box].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor))
            val res = new Box(x.asInstanceOf[Box].getWidth, x.asInstanceOf[Box].getHeight, x.asInstanceOf[Box].getDepth)
            res.setMaterial(mt)
            res.setTranslateX(x.getTranslateX)
            res.setTranslateY(x.getTranslateY)
            res.setTranslateZ(x.getTranslateZ)
            res.setDrawMode(x.asInstanceOf[Box].getDrawMode)
            worldRoot.getChildren.add(res)
          })

      }
    }


    def partition(section: Section): Node = {

      val slice = new Box(((section._1._1._1) + (section._1._2)), ((section._1._1._2) + (section._1._2)), ((section._1._1._3) + (section._1._2)))
      slice.setTranslateX((section._1._2) / 2)
      slice.setTranslateY((section._1._2) / 2)
      slice.setTranslateZ((section._1._2) / 2)
      slice.setMaterial(greenMaterial)
      slice.setDrawMode(DrawMode.LINE)
      slice
    }

    def checkpartition(oc: Octree[Placement], obj: Node): Unit = {
      oc match {
        case OcEmpty => Nil
        case OcNode(coords, leaf1, leaf2, leaf3, leaf4, leaf5, leaf6, leaf7, leaf8) =>
          checkpartition(leaf1, obj)
          checkpartition(leaf2, obj)
          checkpartition(leaf3, obj)
          checkpartition(leaf4, obj)
          checkpartition(leaf5, obj)
          checkpartition(leaf6, obj)
          checkpartition(leaf7, obj)
          checkpartition(leaf8, obj)
        case OcLeaf(section: Section) =>
          worldRoot.getChildren.remove(section._2(0))
          val slice = partition(section)
          if (obj.getBoundsInParent.intersects(slice.getBoundsInParent)) {
            slice.asInstanceOf[Box].setMaterial(greenMaterial)
            worldRoot.getChildren.add(slice)
          } else {
            slice.asInstanceOf[Box].setMaterial(blueMaterial)
            worldRoot.getChildren.add(slice)
          }
      }
    }

    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      //checkpartition(oct1,camVolume)
      scaleOctree(2, oct1)
      //mapColourEffect(sepia, oct1)
      worldRoot.getChildren.removeAll()
    })

  }

  override def init(): Unit = {
    println("init")
  }

  override def stop(): Unit = {
    println("stopped")
  }

}


  object FxApp {

    def main(args: Array[String]): Unit = {
      Application.launch(classOf[Main], args: _*)
      val res = new Box(10, 7, 5)
      val mt = new PhongMaterial()
      mt.setDiffuseColor(Color.rgb(100, 100, 100))
      res.setMaterial(mt)
      res.setTranslateX(2)
      res.setTranslateY(2)
      res.setTranslateZ(2)

      val r = res.getHeight
     // println(r)
    }
  }

