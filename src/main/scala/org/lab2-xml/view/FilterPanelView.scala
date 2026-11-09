package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.control.*
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.geometry.{Insets, Pos}
import scalafx.collections.ObservableBuffer
import org.lab2.tree.TreeNode
import org.lab2.filter.{FilterItem, GrepFilter, RangeFilter, ConjunctiveFilter}
import org.lab2.controller.ControllerState

class GrepFilterRowView(
  displayName: String,
  val filter: GrepFilter,
  val filterItem: FilterItem
) extends VBox:
  spacing = 2

  val checkBox = new CheckBox(displayName):
    selected <==> filterItem.enabled

  val comboBox = new ComboBox[String]():
    maxWidth = Double.MaxValue
    editable = true
    promptText = "Select or type..."

  comboBox.value.onChange { (_, _, newVal) =>
    filter.query = Option(newVal).getOrElse("").trim
  }

  def setPossibleValues(values: List[String]): Unit =
    val current = comboBox.value.value
    comboBox.items = ObservableBuffer.from("" :: values)
    if current != null && values.contains(current) then
      comboBox.value = current

  children = Seq(checkBox, comboBox)

class RangeFilterRowView(
  displayName: String,
  val filter: RangeFilter,
  val filterItem: FilterItem
) extends VBox:
  spacing = 2

  val checkBox = new CheckBox(displayName):
    selected <==> filterItem.enabled

  val fromField = new TextField:
    promptText = "Min"
    prefWidth = 65

  val toField = new TextField:
    promptText = "Max"
    prefWidth = 65

  fromField.text.onChange { (_, _, newVal) =>
    filter.min = Option(newVal).flatMap(_.trim.toDoubleOption)
  }

  toField.text.onChange { (_, _, newVal) =>
    filter.max = Option(newVal).flatMap(_.trim.toDoubleOption)
  }

  private val inputs = new HBox(5, new Label("from"), fromField, new Label("to"), toField):
    alignment = Pos.CenterLeft

  children = Seq(checkBox, inputs)

class FilterPanelView(onApplyFilters: () => Unit) extends VBox:
  spacing = 10
  padding = Insets(10)
  prefWidth = 240
  style = "-fx-background-color: #ffffff; -fx-border-color: #dee2e6; -fx-border-width: 0 1 0 0;"

  private val titleLabel = new Label("Filters"):
    style = "-fx-font-weight: bold; -fx-font-size: 13px;"

  // 1. Full name
  val fullNameFilter = GrepFilter(List("full_name"))
  val fullNameItem = FilterItem("Full Name", fullNameFilter)
  val fullNameRow = GrepFilterRowView("Full Name", fullNameFilter, fullNameItem)

  // 2. Faculty
  val facultyFilter = GrepFilter(List("faculty", "department"))
  val facultyItem = FilterItem("Faculty", facultyFilter)
  val facultyRow = GrepFilterRowView("Faculty", facultyFilter, facultyItem)

  // 3. Chair
  val chairFilter = GrepFilter(List("chair", "department"))
  val chairItem = FilterItem("Chair", chairFilter)
  val chairRow = GrepFilterRowView("Chair", chairFilter, chairItem)

  // 4. Course Year
  val yearFilter = RangeFilter(List("course_year", "department"))
  val yearItem = FilterItem("Course Year", yearFilter)
  val yearRow = RangeFilterRowView("Course Year", yearFilter, yearItem)

  // 5. Group
  val groupFilter = GrepFilter(List("group", "department"))
  val groupItem = FilterItem("Group", groupFilter)
  val groupRow = GrepFilterRowView("Group", groupFilter, groupItem)

  // 6. Course Title
  val titleFilter = GrepFilter(List("title", "course", "courses"))
  val titleItem = FilterItem("Course Title", titleFilter)
  val titleRow = GrepFilterRowView("Course Title", titleFilter, titleItem)

  // 7. Grade
  val gradeFilter = RangeFilter(List("grade", "course", "courses"))
  val gradeItem = FilterItem("Course Grade", gradeFilter)
  val gradeRow = RangeFilterRowView("Course Grade", gradeFilter, gradeItem)

  // 8. Grade Letter
  val letterFilter = GrepFilter(List("grade_letter", "course", "courses"))
  val letterItem = FilterItem("Grade Letter", letterFilter)
  val letterRow = GrepFilterRowView("Grade Letter", letterFilter, letterItem)

  private val grepRows = Seq(fullNameRow, facultyRow, chairRow, groupRow, titleRow, letterRow)

  // Populate ConjunctiveFilter in ControllerState
  val conjunctive = ConjunctiveFilter(scala.collection.mutable.Buffer(
    fullNameItem,
    facultyItem,
    chairItem,
    yearItem,
    groupItem,
    titleItem,
    gradeItem,
    letterItem
  ))
  ControllerState.filter.value = conjunctive

  val applyButton = new Button("Apply Filters"):
    maxWidth = Double.MaxValue
    style = "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-font-weight: bold;"
    onAction = _ => onApplyFilters()

  val filterList = new VBox:
    spacing = 10
    children = Seq(
      fullNameRow,
      facultyRow,
      chairRow,
      yearRow,
      groupRow,
      titleRow,
      gradeRow,
      letterRow
    )

  val scrollPane = new ScrollPane:
    content = filterList
    fitToWidth = true
    vgrow = Priority.Always
    style = "-fx-background-color: transparent;"

  children = Seq(titleLabel, scrollPane, applyButton)

  def updatePossibleValues(tree: TreeNode): Unit =
    grepRows.foreach { row =>
      val values = tree.getPossibleValues(row.filter.targetQuery)
      row.setPossibleValues(values)
    }
