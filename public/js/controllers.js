/* global define */

'use strict';

define(['angular', 'ace'], function(angular) {
  
  console.log(ace)
  
  /* Controllers */

  var appControllers = angular.module('koansControllers', [])

  appControllers.controller('EditorController', ['$scope', function($scope) {
    // set up ace
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/scala");
    editor.setFontSize('14px')
  }])

});