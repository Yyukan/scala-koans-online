/* global define */

'use strict';

define(['angular', '../controllers'], function(angular, appControllers) {

  if (!ace) throw Error('ace editor is not loaded!')

  var compileKoanTxt = "<br>************ Compiling koan ... ************<br>"
    
  var EditorController = function($scope, Suite, Koan, Compiler, ansi2html,
          $filter, $sce) {
    // set up ace
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/scala");
    editor.setFontSize('14px')

    // initialize statistics storage
    if (localStorage['completedKoans'] == undefined) {
      localStorage['completedKoans'] = 0;
    }

    if (localStorage['completedSuites'] == undefined) {
      localStorage['completedSuites'] = JSON.stringify(new Array());
    }

    // display statistics
    $scope.completedKoans = localStorage['completedKoans'];
    $scope.completedSuites = JSON.parse(localStorage['completedSuites']).length;

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
      console.log("Select suite [" + suiteToSelect.name + "]");
      $scope.selectSuite(suiteToSelect)
    })

    // make console hide out
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
        $scope.selectKoan(suite.selectedKoan || suite.koans[0])
      });
    };

    // select koan
    $scope.selectKoan = function(id) {
      var suite = $scope.suite
      
      suite.selectedKoan = id
      suite.saveState()
      
      Koan.get({
        suite: suite.name,
        koan: id
      }, function(koan) {
        
        if ($scope.koan) {
          $scope.koan.saveState()          
        }
        
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
          koan.content = editor.getValue()
          
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

              if(suite.addResolved(koan.id)) {
                  // update statistics (completed koans and suites)
                  localStorage['completedKoans']++;

                  if (suite.isComplete()) {
                    var tmp = JSON.parse(localStorage['completedSuites']);
                    tmp.push(suite.name);
                    localStorage['completedSuites'] = JSON.stringify(tmp);
                  }

                  $scope.completedKoans = localStorage['completedKoans'];
                  $scope.completedSuites = JSON.parse(localStorage['completedSuites']).length;
              }
            }

            // save state
            suite.saveState()
            koan.saveState()

            var html = ansi2html.toHtml(result.output).split('\n').join('<br>')
            $scope.consoleText = $sce.trustAsHtml($scope.consoleText + html)
            $('#console').animate({
              scrollTop: $("#console")[0].scrollHeight
            }, "slow");
          })
        }
        
        koan.restoreState()

        // set editor content (koan context and koan code block)
        // TODO a problem with restoring state for suite.context
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
      'Compiler', 'ansi2html', '$filter', '$sce', EditorController]);
  
});