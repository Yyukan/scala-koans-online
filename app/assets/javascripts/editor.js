var editor
$(function() {
	editor = ace.edit("editor");
	editor.setTheme("ace/theme/eclipse");
	editor.getSession().setMode("ace/mode/scala");

	var currentKoanId = $("koan-id").text()
	selectKoan(currentKoanId)
	$("#prevKoan").parent().addClass("disabled")

	var suite = JSON.parse($("suite").text())
	console.log(suite)

	var selector = new Selector(parseInt(currentKoanId))

	$("#nextKoan").click(function() {
		$("#prevKoan").parent().removeClass("disabled")
		selectKoan(selector.next())
		if (!selector.hasNext()) {
			$(this).parent().addClass("disabled")			
		}
	})
	$("#prevKoan").click(function() {
		$("#nextKoan").parent().removeClass("disabled")
		selectKoan(selector.prev())
		if (!selector.hasPrev()) {
			$(this).parent().addClass("disabled")			
		}
	})
})

function Selector(curId) {
	this.ids = JSON.parse($("suite").text()).ids
	this.index = this.ids.indexOf(curId)

	this.current = function() {
		return this.ids[this.index]
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