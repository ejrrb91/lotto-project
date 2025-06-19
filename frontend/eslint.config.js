import globals from 'globals'
import pluginJs from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'

export default [
  {
    // 이 linterOptions 블록이 핵심.
    // IDE 플러그인이 reportUnusedDisableDirectives 라는 옛날 옵션을 사용하려고 할 때,
    // 최신 ESLint가 이 부분을 보고 "아, 이 옵션은 'error' 레벨로 처리하면 되는구나"라고
    // 알아들을 수 있도록 명시적으로 설정.
    linterOptions: {
      reportUnusedDisableDirectives: 'error',
    },
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
      },
    },
  },
  // 기본 JavaScript 규칙 세트
  pluginJs.configs.recommended,
  // Vue.js 필수 규칙 세트
  ...pluginVue.configs['flat/essential'],
]
