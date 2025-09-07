import tseslint from 'typescript-eslint'
import vue from 'eslint-plugin-vue'
import vueParser from 'vue-eslint-parser'
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended'

/** @type {import('eslint').Linter.FlatConfig[]} */
export default tseslint.config(
  // 全局忽略文件
  {
    ignores: ['dist', 'node_modules'],
  },
  ...tseslint.configs.recommended,

  vue.configs['flat/recommended'],

  // 针对 .vue 文件的特殊配置
  {
    files: ['**/*.vue,ts,js'],
    languageOptions: {
      // 指定 vue 文件的解析器
      parser: vueParser,
      parserOptions: {
        // vue-eslint-parser 会使用这个解析器来解析 <script> 标签
        parser: tseslint.parser,
        sourceType: 'module',
      },
    },
  },

  // 自定义规则
  {
    rules: {
      'no-console': 'warn',
    },
  },

  // ⚠️ Prettier 必须放在最后
  eslintPluginPrettierRecommended
)
