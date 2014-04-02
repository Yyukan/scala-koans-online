/* global define, angular */

'use strict';

require.config({
  paths: {
    // TODO should not be needed after play-webjars upgrade
    // @see https://github.com/webjars/webjars-play#requirejs
    ace: '../../webjars/ace/01.08.2014/src-noconflict/ace'
  }
})

// load libs
require(['angular', 'angular-route', './controllers', './services',
    './directives'], function(angular) {

  // Declare app level module which depends on filters, and services
  var app = angular.module('koansApp', ['ngRoute', 'koansControllers',
      'koansServices', 'koansDirectives']);

  app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/home', {
      templateUrl: 'partials/home.html'
    }).when('/editor', {
      templateUrl: 'partials/editor.html',
      controller: 'EditorController'
    }).when('/admin', {
      templateUrl: 'partials/admin.html',
      controller: 'AdminController'
    }).otherwise({
      redirectTo: '/home'
    });
  }]);

  angular.bootstrap(document, ['koansApp']);

});