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

  // ============ ADMIN CTRL ==================

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
      'AdminKoans', adminController]);

  // ============ EDITOR CTRL ==================

  var editorController = function($scope, Suite, Koan, Compiler, ansi2html,
          $filter, $sce) {
    // set up ace
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/scala");
    editor.setFontSize('14px')

    // display suites
    Suite.query(function(suites) {
      $scope.suites = $filter('orderBy')(suites, 'name')
      console.log("Loaded [" + $scope.suites.length + "] suites...")

      var suiteToSelect = $scope.suites[0]

      // restore last suite from storage
      var lastSuiteName = localStorage['lastSuite']
      if (lastSuiteName) {
        var found = $scope.suites.filter(function(suite) {
          return suite.name === lastSuiteName;
        })[0]
        if (found) {
          suiteToSelect = found
        }
      }

      $scope.selectSuite(suiteToSelect)
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
        // save to local storage
        localStorage["lastSuite"] = suite.name
        
        suite.selected = true;
        suite.restoreState()

        if ($scope.suite) {
          $scope.suite.saveState()
        }

        $scope.suite = suite;
        $scope.selectKoan(suite.koans[0])
      });
    };

    // select koan
    $scope.selectKoan = function(id) {
      var suite = $scope.suite
      suite.selectedKoan = id
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

        // go to next koan
        koan.next = function() {
          if (!koan.isLast) {
            $scope.selectKoan(++suite.selectedKoan)
          }
        }

        // go to previous koan
        koan.prev = function() {
          if (!koan.isFirst) {
            $scope.selectKoan(--suite.selectedKoan)
          }
        }

        // compile koan
        koan.compile = function() {
          // TODO we can rewrite it to use only angular
          // and to remove one dependency
          $('#console').offcanvas('show')
          $scope.consoleText = $sce.trustAsHtml($scope.consoleText
                  + compileKoanTxt)
          $('#console').animate({
            scrollTop: $("#console")[0].scrollHeight
          }, "slow");

          Compiler.compile({
            koan: editor.getValue(),
            suite: suite.name
          }, function(result) {

            if (result.returnCode === 0) {
              suite.addResolved(koan.id)
              suite.saveState()
            }

            var html = ansi2html.toHtml(result.output).split('\n').join('<br>')
            $scope.consoleText = $sce.trustAsHtml($scope.consoleText + html)
            $('#console').animate({
              scrollTop: $("#console")[0].scrollHeight
            }, "slow");
          })
        }

        // set editor content (koan context and koan code block)
        if (suite.context != "") {
          editor.setValue(suite.context + "\n\n" + koan.content);
        } else {
          editor.setValue(koan.content);
        }
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
      'Compiler', 'ansi2html', '$filter', '$sce', editorController]);

});