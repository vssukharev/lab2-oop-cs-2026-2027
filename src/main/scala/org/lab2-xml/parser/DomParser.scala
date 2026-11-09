
package org.lab2

import javax.xml.parsers.DocumentBuilderFactory
import java.io.File
import org.w3c.dom

class DomParser extends Parser:
  import dom.*

  private var _doc: Document = null
    
