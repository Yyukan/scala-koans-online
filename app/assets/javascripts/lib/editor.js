var editor
var selector

// set up json
$.ajaxSetup({
  contentType: "application/json"
})

// set up ace
editor = ace.edit("editor");
editor.setTheme("ace/theme/eclipse");
editor.getSession().setMode("ace/mode/scala");

// map buttons
var currentKoan = $("koan-id").text()
var currentSuite = $("suite").text()

selector = new Selector()
selectSuite($('.dropdown > a'))

$("#nextKoan").click(function() {
  selectKoan(selector.suiteId, selector.next())
  updatePrevAndNext()
})
$("#prevKoan").click(function() {
  selectKoan(selector.suiteId, selector.prev())
  updatePrevAndNext()
})

$("#suites").children().click(function() {
  var li = $(this)
  if (!li.hasClass("disabled")) {
    selectSuite(li)
  }
})

$("#compile").click(function() {
  compile()
})

function Selector() {
  // TODO:oshtykhno implement next/prev functionality
  this.ids = null
  this.index = 0
  this.suiteId = null

  this.current = function(id) {
    if (id) {
      this.index = this.ids.indexOf(id)
    } else {
      return this.ids[this.index]
    }
  }

  this.next = function() {
    if (this.hasNext()) {
      this.index += 1
    }
    return this.current()
  }

  this.hasNext = function() {
    return this.ids.length > this.index + 1
  }

  this.prev = function() {
    if (this.hasPrev()) {
      this.index -= 1
    }
    return this.current()
  }

  this.hasPrev = function() {
    return 0 <= this.index - 1
  }
}

function selectKoan(suite, koan) {
  $.ajax({
    url: "koans/" + suite + "/" + koan,
    success: function(data) {
      editor.setValue(data.content)
      editor.gotoLine(0);
    },
    dataType: "json"
  });
}

function updatePrevAndNext() {
  var next = $("#nextKoan").parent()
  var prev = $("#prevKoan").parent()
  next.removeClass("disabled")
  prev.removeClass("disabled")
  if (!selector.hasNext()) {
    next.addClass("disabled")
  }
  if (!selector.hasPrev()) {
    prev.addClass("disabled")
  }
}

function selectSuite(suite) {
  var suiteId = suite.attr("suiteId")
  $("#selectedSuite").attr("suiteId", suiteId)
  $("#selectedSuite").text(suite.children("a").text())
  $("#suites").children().removeClass("disabled")
  $("#suites").children("li[suiteId=" + suiteId + "]").addClass("disabled")
  $.ajax({
    url: "koans/" + suiteId,
    success: function(koanIds) {
      selector.ids = koanIds
      // TODO if no koans?
      selector.suiteId = suiteId
      selector.current(koanIds[0])
      selectKoan(suiteId, koanIds[0])
      updatePrevAndNext()
    },
    dataType: "json"
  });
}

function compile() {
  var koan = editor.getValue()
  $.ajax({
    type: "POST",
    url: "koans/compile",
    data: JSON.stringify({
      koan: koan
    }),
    success: function(result) {
      console.log(result)
    },
    dataType: "json"
  });
}