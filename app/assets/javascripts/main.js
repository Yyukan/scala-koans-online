require.config({
  paths: {
    'editor': 'lib/editor'
  }
})

$(function() {
  require(['editor'], function(editor) {
    console.log('loaded')
  })
})