require.config({
  paths: {
    'editor': 'lib/editor'
  }
})

$(function() {
  require(['editor'], function(editor) {
    //console.log('editor is loaded')
  })
})