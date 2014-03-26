/* global define */

'use strict';

define(['angular', 'ace'], function(angular) {

  if (!ace) throw Error('ace editor is not loaded!')

  /* Controllers */

  var appControllers = angular.module('koansControllers', [])

  appControllers.controller('EditorController', ['$scope', 'Suite',
      function($scope, Suite) {
        // set up ace
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/eclipse");
        editor.getSession().setMode("ace/mode/scala");
        editor.setFontSize('14px')

        $scope.suites = Suite.query(function(suites) {
          $scope.selectedSuite = suites[0]
          $scope.selectedSuite.selected = true
        })
      }])

});