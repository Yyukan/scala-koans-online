/* global define */

'use strict';

define(['angular', 'ace', 'ui-bootstrap'], function(angular) {

  if (!ace) throw Error('ace editor is not loaded!')

  /* Controllers */

  var appControllers = angular.module('koansControllers', ['ui.bootstrap'])

  appControllers.controller('EditorController', ['$scope', 'Suite', 'Koan',
      function($scope, Suite, Koan) {

        // set up ace
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/eclipse");
        editor.getSession().setMode("ace/mode/scala");
        editor.setFontSize('14px')

        // display suites
        $scope.suites = Suite.query(function(suites) {
          $scope.selectSuite(suites[0])
        })

        // set up tooltips
        $("[title]").tooltip()

        // make console hideable
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

          suite.selected = true;
          $scope.selectedSuite = suite;

          Suite.get(suite, function(suite) {
            $scope.koan = Koan.get({
              suite: suite.name,
              koan: suite.koans[0]
            }, function(koan) {
              editor.setValue(koan.content);
              editor.gotoLine(0);
            });
          });
        };

        // search suite
        $scope.search = function() {
          var suite = $scope.searchedSuite
          if (suite) {
            $scope.selectSuite(suite)
          }
          $scope.searchedSuite = ''
        }

      }]);

});