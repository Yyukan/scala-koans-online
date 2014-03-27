/* global define */

'use strict';

define(['angular'], function(angular) {
  var directives = angular.module('koansDirectives', []);

  directives.directive('shortcut', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: true,
      link: function postLink($scope, iElement, iAttrs) {
        $(document).on('keypress', function(e) {
          $scope.$apply($scope.keyPressed(e));
        });
      }
    };
  });

});