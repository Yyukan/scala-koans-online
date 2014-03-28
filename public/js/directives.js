/* global define */

'use strict';

define(['angular'], function(angular) {
  var directives = angular.module('koansDirectives', []);

  directives.directive('shortcut', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: true,
      link: function postLink(scope, iElement, iAttrs) {
        $(document).on('keypress', function(e) {
          scope.$apply(scope.keyPressed(e));
        });
      }
    };
  });
  
  directives.directive('focusMe', function($timeout, $parse) {
    return {
      link: function(scope, element, attrs) {
        scope.$watch(attrs.focusMe, function(value) {
          if(value === true) { 
            $timeout(function() {
              element[0].focus();
              scope[attrs.focusMe] = false;
            });
          }
        });
      }
    }
  });

});