<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const router = useRouter()

// Check if current route is auth page (login/register)
const isAuthPage = computed(() => {
  return router.currentRoute.value.path === '/login' || router.currentRoute.value.path === '/register'
})
</script>

<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #0f0f14;
  color: #e4e4e7;
}

#app {
  min-height: 100vh;
  background-color: #0f0f14;
}

/* Dark theme overrides for Element Plus */
.el-card {
  background-color: #1a1a24 !important;
  border: 1px solid #2a2a3a !important;
  color: #e4e4e7 !important;
}

.el-card__header {
  border-bottom: 1px solid #2a2a3a !important;
  color: #e4e4e7 !important;
}

.el-table {
  background-color: #1a1a24 !important;
  color: #e4e4e7 !important;
}

.el-table tr {
  background-color: #1a1a24 !important;
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background-color: #1f1f2e !important;
}

.el-table th.el-table__cell {
  background-color: #15151f !important;
  color: #a1a1aa !important;
}

.el-table td.el-table__cell {
  border-bottom: 1px solid #2a2a3a !important;
}

.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell {
  background-color: #252538 !important;
}

.el-dialog {
  background-color: #1a1a24 !important;
  border: 1px solid #2a2a3a !important;
}

.el-dialog__header {
  color: #e4e4e7 !important;
}

.el-dialog__title {
  color: #e4e4e7 !important;
}

.el-form-item__label {
  color: #a1a1aa !important;
}

.el-input__inner {
  background-color: #15151f !important;
  border-color: #2a2a3a !important;
  color: #e4e4e7 !important;
}

.el-input__wrapper {
  background-color: #15151f !important;
  box-shadow: 0 0 0 1px #2a2a3a inset !important;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #4a4a6a inset !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #6366f1 inset !important;
}

.el-select .el-input__wrapper {
  background-color: #15151f !important;
}

.el-textarea__inner {
  background-color: #15151f !important;
  box-shadow: 0 0 0 1px #2a2a3a inset !important;
  color: #e4e4e7 !important;
}

.el-empty__description p {
  color: #71717a !important;
}

.el-statistic__head {
  color: #a1a1aa !important;
}

.el-statistic__content {
  color: #e4e4e7 !important;
}

.el-divider {
  border-color: #2a2a3a !important;
}

.el-dropdown-menu {
  background-color: #1a1a24 !important;
  border: 1px solid #2a2a3a !important;
}

.el-dropdown-menu__item {
  color: #e4e4e7 !important;
}

.el-dropdown-menu__item:hover {
  background-color: #252538 !important;
}

.el-select-dropdown {
  background-color: #1a1a24 !important;
  border: 1px solid #2a2a3a !important;
}

.el-select-dropdown__item {
  color: #e4e4e7 !important;
}

.el-select-dropdown__item.hover,
.el-select-dropdown__item:hover {
  background-color: #252538 !important;
}

.el-date-picker {
  background-color: #1a1a24 !important;
}

.el-picker-panel {
  background-color: #1a1a24 !important;
  border: 1px solid #2a2a3a !important;
  color: #e4e4e7 !important;
}

.el-input-number {
  background-color: #15151f !important;
}

.el-message-box {
  background-color: #1a1a24 !important;
  border: 1px solid #2a2a3a !important;
}

.el-message-box__title {
  color: #e4e4e7 !important;
}

.el-message-box__content {
  color: #a1a1aa !important;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #15151f;
}

::-webkit-scrollbar-thumb {
  background: #2a2a3a;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #3a3a4a;
}
</style>
