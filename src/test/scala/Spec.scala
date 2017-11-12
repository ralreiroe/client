import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

trait Spec
  extends FreeSpecLike
    with MustMatchers
    with DiagrammedAssertions
    with TryValues
    with OptionValues
    with AppendedClues
    with ScalaFutures
