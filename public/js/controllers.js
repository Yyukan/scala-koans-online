/* global define */

'use strict';

var deps = ['angular', 'ace', 'ui-bootstrap', 'angular-sanitize',
    'ext/ansi2html']

define(deps, function(angular) {

  return angular.module('koansControllers', ['ui.bootstrap',
      'ngSanitize', 'ansiToHtml'])

});