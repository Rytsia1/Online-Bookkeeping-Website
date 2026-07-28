<template>
  <button
    class="theme-toggle"
    :title="isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'"
    :aria-label="isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'"
    @click="themeStore.toggleTheme()"
  >
    <transition name="icon-fade" mode="out-in">
      <!-- Moon icon — shown in dark mode -->
      <svg v-if="isDark" key="moon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
      </svg>
      <!-- Sun icon — shown in light mode -->
      <svg v-else key="sun" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="5"/>
        <line x1="12" y1="1"  x2="12" y2="3"/>
        <line x1="12" y1="21" x2="12" y2="23"/>
        <line x1="4.22" y1="4.22"   x2="5.64"  y2="5.64"/>
        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
        <line x1="1" y1="12" x2="3"  y2="12"/>
        <line x1="21" y1="12" x2="23" y2="12"/>
        <line x1="4.22" y1="19.78"  x2="5.64"  y2="18.36"/>
        <line x1="18.36" y1="5.64"  x2="19.78" y2="4.22"/>
      </svg>
    </transition>
  </button>
</template>

<script setup>
import { computed } from 'vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
const isDark = computed(() => themeStore.theme === 'dark')
</script>

<style scoped>
.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: transparent;
  border: 1px solid var(--wire);
  border-radius: 3px;
  color: var(--ash);
  cursor: pointer;
  transition: border-color 0.15s, color 0.15s, background 0.15s;
  flex-shrink: 0;
}

.theme-toggle:hover {
  border-color: var(--ember);
  color: var(--ember);
  background: rgba(240, 90, 20, 0.07);
}

/* Icon swap animation */
.icon-fade-enter-active,
.icon-fade-leave-active { transition: opacity 0.15s ease, transform 0.15s ease; }
.icon-fade-enter-from   { opacity: 0; transform: scale(0.6) rotate(-30deg); }
.icon-fade-leave-to     { opacity: 0; transform: scale(0.6) rotate(30deg); }
</style>
