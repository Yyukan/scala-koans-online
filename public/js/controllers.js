/* global define */

'use strict';

var deps = ['angular', 'ace', 'ui-bootstrap', 'angular-sanitize',
    'ext/ansi2html']

define(deps, function(angular) {

  if (!ace) throw Error('ace editor is not loaded!')

  var compileKoanTxt = "<br>************ Compiling koan ... ************<br>"

  /* Controllers */

  var appControllers = angular.module('koansControllers', ['ui.bootstrap',
      'ngSanitize', 'ansiToHtml'])

  var adminController = function($scope, Suite, Koan, AdminKoans) {
    // display suites
    $scope.suites = Suite.query()
    // display koans
    $scope.koans = Koan.query()
    // upload koans
    $scope.uploadKoans = function() {
      $scope.isUploading = true
      AdminKoans.upload(function() {
        $scope.isUploading = false
      })
    }
  };

  appControllers.controller('AdminController', ['$scope', 'Suite', 'Koan',
      'AdminKoans', 'ansi2html', adminController]);

  var editorController = function($scope, Suite, Koan, Compiler, ansi2html) {
    // set up ace
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/scala");
    editor.setFontSize('14px')

    // display suites
    $scope.suites = Suite.query(function(suites) {
      $scope.selectSuite(suites[0])
    })

    // make console hideable
    $scope.consoleText = "Welcome to Scala Interpreter!<br>"
    $('#console').hover(function() {
      $(this).offcanvas('show')
    })
    $("#console").animate({
      scrollTop: $("#console")[0].scrollHeight
    }, "slow");

    // select suite function
    $scope.selectSuite = function(suite) {
      $scope.suites.forEach(function(suite) {
        suite.selected = false
      });

      Suite.get(suite, function(suite) {
        suite.selected = true;
        $scope.suite = suite;
        $scope.selectKoan(suite.koans[0])
      });
    };

    // select koan
    $scope.selectKoan = function(id) {
      var suite = $scope.suite
      Koan.get({
        suite: suite.name,
        koan: id
      }, function(koan) {
        $scope.koan = koan

        // koans navigation
        var suite = $scope.suite
        var koans = suite.koans
        koan.isFirst = koans[0] === koan.id
        koan.isLast = koans[koans.length - 1] === koan.id
        koan.next = function() {
          if (!koan.isLast) {
            $scope.selectKoan(koans.next())
          }
        }
        koan.prev = function() {
          if (!koan.isFirst) {
            $scope.selectKoan(koans.prev())
          }
        }
        koan.compile = function() {
          $('#console').offcanvas('show')
          $scope.consoleText += compileKoanTxt
          $('#console').animate({
            scrollTop: $("#console")[0].scrollHeight
          }, "slow");

          Compiler.compile({
            koan: editor.getValue()
          }, function(result) {
            $scope.consoleText += ansi2html.toHtml(result.output).split('\n')
                    .join('<br>')
            $('#console').animate({
              scrollTop: $("#console")[0].scrollHeight
            }, "slow");
          })
        }

        // set editor content
        editor.setValue(koan.content);
        editor.gotoLine(0);

        // shortcuts
        $scope.keyPressed = function(e) {
          // console.log(e)
          if (e.ctrlKey && e.keyCode === 10) {
            $scope.koan.compile()
          }
          if (e.ctrlKey && e.keyCode === 28) {
            $scope.focusSearch = true
          }
        }
      })
    };

    // search suite
    $scope.search = function() {
      var suite = $scope.searchedSuite
      if (suite) {
        $scope.selectSuite(suite)
      }
      $scope.searchedSuite = ''
      $scope.focusSearch = false
    }
  };

  appControllers.controller('EditorController', ['$scope', 'Suite', 'Koan',
      'Compiler', 'ansi2html', editorController]);

});