/* global define */

'use strict';

define(['angular', '../controllers'], function(angular, appControllers) {

  var compileKoanTxt = "<br>************ Compiling koan ... ************<br>"

  var ConsoleController = function($scope, $rootScope, Compiler, ansi2html,
          $sce) {
    // make console hide out
    $scope.consoleText = "Welcome to Scala Interpreter!<br>"
    $('#console').hover(function() {
      $(this).offcanvas('show')
    })
    $("#console").animate({
      scrollTop: $("#console")[0].scrollHeight
    }, "slow");

    $rootScope.$on('compile', function(event, suite, koan, content) {

      // TODO we can rewrite it to use only angular
      // and to remove one dependency
      $('#console').offcanvas('show')
      $scope.consoleText = $sce
              .trustAsHtml($scope.consoleText + compileKoanTxt)
      $('#console').animate({
        scrollTop: $("#console")[0].scrollHeight
      }, "slow");

      Compiler.compile({
        koan: content,
        suite: suite.name
      }, function(result) {

        if (result.returnCode === 0) {
          $rootScope.$emit('completeKoan', koan);
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
    });

  };

  var deps = ['$scope', '$rootScope', 'Compiler', 'ansi2html', '$sce',
      ConsoleController];
  appControllers.controller('ConsoleController', deps);
});