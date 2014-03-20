// set up json
$.ajaxSetup({
  contentType: "application/json"
})

var editor
var selector

// set up ace
editor = ace.edit("editor");
editor.setTheme("ace/theme/eclipse");
editor.getSession().setMode("ace/mode/scala");
editor.setFontSize('14px')

// typeahead
var suites = new Bloodhound({
  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
  queryTokenizer: Bloodhound.tokenizers.whitespace,
  prefetch: 'suites/list',
  remote: 'suites/list/%QUERY'
});

// kicks off the loading/processing
suites.initialize();

$('.typeahead').typeahead({
  minLength: 1,
  highlight: true,
  hint: true
}, {
  name: 'suites',
  displayKey: 'name',
  source: suites.ttAdapter()
});


$("#suitesSearchForm").submit(function(e) {
  e.preventDefault();
  var input = $(e.target[1])
  var suite = input.val()
  if (suite && suite.length > 0) {
    input.val('')
    this.reset()
    selectSuite(suite)    
  }
});

// map buttons
var currentKoan = $("koan-id").text()
var currentSuite = $("suite").text()

selector = new Selector()
selectSuite($('.dropdown > a').text())

$("#nextKoan").click(function() {
  selectKoan(selector.suiteId, selector.next())
})
$("#prevKoan").click(function() {
  selectKoan(selector.suiteId, selector.prev())
})

$("#suites").children().click(function() {
  var li = $(this)
  if (!li.hasClass("disabled")) {
    selectSuite(li.text())
  }
})

$("#compile").click(function() {
  compile()
})

function Selector() {
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
  updatePrevAndNext()
}

function updatePrevAndNext() {
  var next = $("#nextKoan")
  var prev = $("#prevKoan")
  next.removeClass("disabled")
  prev.removeClass("disabled")
  if (!selector.hasNext()) {
    next.addClass("disabled")
  }
  if (!selector.hasPrev()) {
    prev.addClass("disabled")
  }
}

function selectSuite(suiteId) {
  $("#selectedSuite").attr("suiteId", suiteId)
  $("#selectedSuite").text(suiteId)
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
    error: function(result) {
      console.warn(result)
    },
    dataType: "json"
  });
}