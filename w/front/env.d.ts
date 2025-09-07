// Vite env typings
interface ImportMetaEnv {
  readonly VITE_API_URL: string
  // more env vars... 
}
interface ImportMeta {
  readonly env: ImportMetaEnv
}
