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

          Suite.get(suite, function(suite) {
            suite.selected = true;
            $scope.selectedSuite = suite;
            $scope.selectKoan(suite.koans[0])
          });
        };

        // select koan
        $scope.selectKoan = function(id) {
          var suite = $scope.selectedSuite
          Koan.get({
            suite: suite.name,
            koan: id
          }, function(koan) {
            $scope.koan = koan

            // koans navigation
            var suite = $scope.selectedSuite
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
              console.log('compile')
            }

            // set editor content
            editor.setValue(koan.content);
            editor.gotoLine(0);
          })
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