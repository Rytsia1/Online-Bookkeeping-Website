/**
 * Theme Store (Pinia)
 *
 * Priority order for the initial theme:
 *  1. Value saved in localStorage  ('dark' | 'light')
 *  2. Operating-system prefers-color-scheme
 *  3. Fallback → 'dark'
 *
 * The store writes the chosen theme onto
 * document.documentElement as data-theme="dark|light"
 * so that CSS :root[data-theme="..."] selectors pick it up.
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'

const LS_KEY = 'bookkeeping-theme'

/** Returns the resolved initial theme. */
function resolveInitialTheme() {
  const saved = localStorage.getItem(LS_KEY)
  if (saved === 'dark' || saved === 'light') return saved

  // No manual preference stored — check OS setting.
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
    return 'light'
  }
  return 'dark'
}

/** Apply the theme attribute to <html>. */
function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme)
}

export const useThemeStore = defineStore('theme', () => {
  const theme = ref(resolveInitialTheme())

  // Apply immediately on store creation (before first paint).
  applyTheme(theme.value)

  /** Switch to a specific theme and persist the choice. */
  function setTheme(t) {
    theme.value = t
    localStorage.setItem(LS_KEY, t)
    applyTheme(t)
  }

  /** Toggle between dark ↔ light. */
  function toggleTheme() {
    setTheme(theme.value === 'dark' ? 'light' : 'dark')
  }

  /**
   * Listen for OS-level colour-scheme changes.
   * Only reacts when the user has NOT set a manual preference.
   */
  function watchSystemTheme() {
    if (!window.matchMedia) return
    window.matchMedia('(prefers-color-scheme: light)').addEventListener('change', (e) => {
      if (!localStorage.getItem(LS_KEY)) {
        setTheme(e.matches ? 'light' : 'dark')
      }
    })
  }

  watchSystemTheme()

  return { theme, setTheme, toggleTheme }
})
