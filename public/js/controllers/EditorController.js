/* global define */

'use strict';

define(['angular', '../controllers'], function(angular, appControllers) {

  if (!ace) throw Error('ace editor is not loaded!')

  var EditorController = function($scope, $rootScope, Koan) {
    // set up ace
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/scala");
    editor.setFontSize('14px');

    // on select suite from navbar
    $rootScope.$on('selectSuite', function(event, suite) {
      $scope.suite = suite;
      $scope.suite.restoreState();
      $scope.selectKoan(suite.selectedKoan || suite.koans[0])
    });
    
    // on compile from navbar
    $rootScope.$on('compileCurrentKoan', function() {
      if ($scope.koan) {
        $scope.koan.compile();
      }
    });
    
    $rootScope.$on('completeKoan', function(event, koan) {
      $scope.suite.addResolved(koan.id);
    });

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
          koan.content = editor.getValue();
          $rootScope.$emit('compile', suite, koan, editor.getValue());
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
          if (e.ctrlKey && e.keyCode === 10) {
            $scope.koan.compile()
          }
          if (e.ctrlKey && e.keyCode === 28) {
            $scope.focusSearch = true
          }
        }
      })
    };
  };

  var deps = ['$scope', '$rootScope', 'Koan', EditorController];
  appControllers.controller('EditorController', deps);

});