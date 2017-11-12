import scala.collection.SeqView

package object shortcodes3 {

  type ViewSeqString = SeqView[String, Seq[_]]
  type ViewSeq[T] = SeqView[T, Seq[_]]
}
