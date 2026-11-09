
case class TreeNode(name: String, content: String | List[TreeNode]) =

  def getPossibleValues(path: AttributePath, node: Node): Set[String] =
    if path.isEmpty then
      Set(node.getTextContent)
    else
      val children = doc.getElementsByTagName(path.head)
      for
        child <- children.to[Set]
        if child.getNodeType == Node.ELEMENT_NODE
        if child.getNodeName == path.head 
        value <- getPossibleValues(path.tail, child)
      yield
        value


