var editor
var selector

$(function() {
	editor = ace.edit("editor");
	editor.setTheme("ace/theme/eclipse");
	editor.getSession().setMode("ace/mode/scala");

	var currentKoanId = $("koan-id").text()
	selectKoan(currentKoanId)

	selector = new Selector()
	selector.current(parseInt(currentKoanId))

	updatePrevAndNext()

	$("#nextKoan").click(function() {
		selectKoan(selector.next())
		updatePrevAndNext()
	})
	$("#prevKoan").click(function() {
		selectKoan(selector.prev())
		updatePrevAndNext()
	})

	$("#suites").children().click(function() {
		var li = $(this)
		if (!li.hasClass("disabled")) {
			selectSuite(li)
		}
	})
})

function Selector() {
	this.ids = JSON.parse($("suite").text()).koanIds
	this.index = 0

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

function selectKoan(id) {
	$.ajax({
		url : "koan?id=" + id,
		success : function(data) {
			editor.setValue(data.content)
			editor.gotoLine(0);
		},
		dataType : "json"
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
		url : "suite?id=" + suiteId,
		success : function(suite) {
			selector.ids = suite.koanIds
			// TODO if no koans?
			selector.current(suite.koanIds[0])
			selectKoan(suite.koanIds[0])
			updatePrevAndNext()
		},
		dataType : "json"
	});
}