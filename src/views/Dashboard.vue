<template>
  <div class="dashboard-container">
    <!-- Header -->
    <header class="dashboard-header">
      <div class="header-left">
        <h1 class="welcome-title">
          Welcome, <span class="username">{{ userName }}</span>
        </h1>
        <p class="welcome-subtitle">{{ currentDate }}</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="btn-primary-custom" @click="navigateToBills">
          View Bills
        </el-button>
        <el-button class="btn-outline-custom" @click="navigateToAnalytics">
          Analytics
        </el-button>
        <el-dropdown @command="handleCommand" trigger="click">
          <el-button class="btn-icon-custom" circle>
            <i class="el-icon-user"></i>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">Profile</el-dropdown-item>
              <el-dropdown-item command="settings">Settings</el-dropdown-item>
              <el-dropdown-item divided command="logout">Logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- Quick Stats -->
    <section class="stats-grid">
      <div class="stat-item">
        <div class="stat-icon stat-icon-total">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">Total Transactions</span>
          <span class="stat-number">{{ totalTransactions }}</span>
        </div>
      </div>

      <div class="stat-item">
        <div class="stat-icon stat-icon-month">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" /><line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" /><line x1="3" y1="10" x2="21" y2="10" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">This Month</span>
          <span class="stat-number">{{ currentMonthCount }}</span>
        </div>
      </div>

      <div class="stat-item">
        <div class="stat-icon stat-icon-status">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">Status</span>
          <span class="stat-number" :class="statusClass">{{ statusText }}</span>
        </div>
      </div>

      <div class="stat-item">
        <div class="stat-icon stat-icon-update">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">Last Updated</span>
          <span class="stat-number">{{ lastUpdate }}</span>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <div class="dashboard-content">
      <!-- Recent Bills -->
      <section class="content-card recent-bills-card">
        <div class="card-header">
          <h3 class="card-title">Recent Bills</h3>
          <el-button link class="view-all-link" @click="navigateToBills">View All →</el-button>
        </div>

        <div class="table-wrapper">
          <el-table
            :data="recentBills"
            stripe
            style="width: 100%"
            :header-cell-style="tableHeaderStyle"
            :cell-style="tableCellStyle"
            :default-sort="{ prop: 'date', order: 'descending' }"
          >
            <el-table-column prop="date" label="Date" width="110" sortable />
            <el-table-column prop="type" label="Type" width="120">
              <template #default="{ row }">
                <span :class="['type-badge', row.type === 'income' ? 'badge-income' : 'badge-expense']">
                  {{ row.type === 'income' ? 'Income' : 'Expense' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="Category" width="120" />
            <el-table-column prop="amount" label="Amount" width="140">
              <template #default="{ row }">
                <span :class="row.type === 'income' ? 'income-text' : 'expense-text'">
                  {{ row.type === 'income' ? '+' : '-' }}{{ formatCurrency(row.amount) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="Description" min-width="180" show-overflow-tooltip />
          </el-table>
        </div>

        <el-empty v-if="recentBills.length === 0" description="No bills yet" class="empty-state" />
      </section>

      <!-- Right Sidebar -->
      <aside class="sidebar-section">
        <!-- Monthly Summary -->
        <section class="content-card summary-card">
          <div class="card-header">
            <h3 class="card-title">Monthly Summary</h3>
          </div>

          <div class="summary-list">
            <div class="summary-row income-row">
              <div class="summary-dot dot-income"></div>
              <div class="summary-detail">
                <span class="summary-label">Total Income</span>
                <span class="summary-value income-text">{{ formatCurrency(monthlySummary.income) }}</span>
              </div>
            </div>

            <div class="summary-row expense-row">
              <div class="summary-dot dot-expense"></div>
              <div class="summary-detail">
                <span class="summary-label">Total Expenses</span>
                <span class="summary-value expense-text">{{ formatCurrency(monthlySummary.expense) }}</span>
              </div>
            </div>

            <div class="summary-divider"></div>

            <div class="summary-row balance-row">
              <div class="summary-dot dot-balance"></div>
              <div class="summary-detail">
                <span class="summary-label">Net Balance</span>
                <span class="summary-value" :class="monthlySummary.balance >= 0 ? 'income-text' : 'expense-text'">
                  {{ formatCurrency(monthlySummary.balance) }}
                </span>
              </div>
            </div>
          </div>

          <el-button type="primary" class="btn-primary-custom full-width" @click="navigateToAnalytics">
            View Full Analytics
          </el-button>
        </section>

        <!-- Quick Actions -->
        <section class="content-card actions-card">
          <div class="card-header">
            <h3 class="card-title">Quick Actions</h3>
          </div>

          <div class="action-buttons">
            <button class="action-btn" @click="navigateToBills">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19" /><line x1="5" y1="12" x2="19" y2="12" />
              </svg>
              Add Bill
            </button>
            <button class="action-btn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" /><polyline points="7 10 12 15 17 10" /><line x1="12" y1="15" x2="12" y2="3" />
              </svg>
              Export Data
            </button>
            <button class="action-btn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="3" /><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z" />
              </svg>
              Settings
            </button>
          </div>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

// State
const bills = ref([])
const monthlySummary = ref({
  income: 0,
  expense: 0,
  balance: 0,
})
const loading = ref(false)

// Table styles
const tableHeaderStyle = {
  background: '#1e1e2e',
  color: '#94a3b8',
  borderBottom: '1px solid #2a2a3a',
  fontWeight: '600',
  fontSize: '13px',
}

const tableCellStyle = {
  background: '#1a1a24',
  color: '#e2e8f0',
  borderBottom: '1px solid #2a2a3a',
}

// Computed
const userName = computed(() => {
  return localStorage.getItem('username') || 'User'
})

const currentDate = computed(() => {
  const today = new Date()
  const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }
  return today.toLocaleDateString('en-US', options)
})

const recentBills = computed(() => {
  return bills.value.slice(0, 5)
})

const totalTransactions = computed(() => {
  return bills.value.length
})

const currentMonthCount = computed(() => {
  const today = new Date()
  const currentMonth = today.getMonth()
  const currentYear = today.getFullYear()

  return bills.value.filter((bill) => {
    const billDate = new Date(bill.date)
    return billDate.getMonth() === currentMonth && billDate.getFullYear() === currentYear
  }).length
})

const statusText = computed(() => {
  if (monthlySummary.value.balance > 0) return 'Surplus'
  if (monthlySummary.value.balance < 0) return 'Deficit'
  return 'Balanced'
})

const statusClass = computed(() => {
  if (monthlySummary.value.balance > 0) return 'income-text'
  if (monthlySummary.value.balance < 0) return 'expense-text'
  return ''
})

const lastUpdate = computed(() => {
  const now = new Date()
  return now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
})

// Methods
const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 0,
  }).format(amount)
}

const fetchBills = async () => {
  try {
    loading.value = true
    const data = await request.get('/bills')
    bills.value = Array.isArray(data) ? data.sort((a, b) => new Date(b.date) - new Date(a.date)) : []
  } catch (error) {
    ElMessage.error('Failed to load bills data')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchMonthlySummary = async () => {
  try {
    const data = await request.get('/stats/summary')
    monthlySummary.value = {
      income: data.income || 0,
      expense: data.expense || 0,
      balance: data.balance || 0,
    }
  } catch (error) {
    console.error(error)
  }
}

const navigateToBills = () => {
  router.push('/bills')
}

const navigateToAnalytics = () => {
  router.push('/analytics')
}

const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/login')
      ElMessage.success('Logged out successfully')
      break
    case 'profile':
      ElMessage.info('Profile feature coming soon')
      break
    case 'settings':
      ElMessage.info('Settings feature coming soon')
      break
  }
}

// Lifecycle
onMounted(() => {
  fetchBills()
  fetchMonthlySummary()
})
</script>

<style scoped>
.dashboard-container {
  padding: 32px;
  max-width: 1440px;
  margin: 0 auto;
  min-height: 100vh;
}

/* Header */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 28px 32px;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 16px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: #f1f5f9;
  margin: 0;
}

.username {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 4px 0 0 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-primary-custom {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  border-radius: 10px;
  font-weight: 600;
  padding: 10px 20px;
  transition: all 0.2s ease;
}

.btn-primary-custom:hover {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
}

.btn-outline-custom {
  background: transparent;
  border: 1px solid #2a2a3a;
  color: #e2e8f0;
  border-radius: 10px;
  font-weight: 500;
  padding: 10px 20px;
  transition: all 0.2s ease;
}

.btn-outline-custom:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: rgba(99, 102, 241, 0.05);
}

.btn-icon-custom {
  background: #1e1e2e;
  border: 1px solid #2a2a3a;
  color: #94a3b8;
  width: 40px;
  height: 40px;
}

.btn-icon-custom:hover {
  border-color: #6366f1;
  color: #6366f1;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.stat-item:hover {
  border-color: #3a3a4a;
  transform: translateY(-2px);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon-total {
  background: rgba(99, 102, 241, 0.15);
  color: #6366f1;
}

.stat-icon-month {
  background: rgba(16, 185, 129, 0.15);
  color: #10b981;
}

.stat-icon-status {
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.stat-icon-update {
  background: rgba(139, 92, 246, 0.15);
  color: #8b5cf6;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #f1f5f9;
}

/* Main Content */
.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.content-card {
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 14px;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0;
}

.view-all-link {
  color: #6366f1 !important;
  font-weight: 500;
  font-size: 13px;
}

/* Table */
.table-wrapper {
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #2a2a3a;
}

.table-wrapper :deep(.el-table) {
  background: #1a1a24;
  --el-table-border-color: #2a2a3a;
}

.table-wrapper :deep(.el-table tr) {
  background: #1a1a24;
}

.table-wrapper :deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: #16161f;
}

.table-wrapper :deep(.el-table td.el-table__cell),
.table-wrapper :deep(.el-table th.el-table__cell.is-leaf) {
  border-bottom: 1px solid #2a2a3a;
}

.table-wrapper :deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background: rgba(99, 102, 241, 0.05);
}

.type-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.badge-income {
  background: rgba(16, 185, 129, 0.15);
  color: #10b981;
}

.badge-expense {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.income-text {
  color: #10b981;
  font-weight: 600;
}

.expense-text {
  color: #ef4444;
  font-weight: 600;
}

.empty-state {
  padding: 40px 0;
}

.empty-state :deep(.el-empty__description p) {
  color: #64748b;
}

/* Sidebar */
.sidebar-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Summary Card */
.summary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 10px;
  background: #12121a;
}

.summary-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-income {
  background: #10b981;
}

.dot-expense {
  background: #ef4444;
}

.dot-balance {
  background: #6366f1;
}

.summary-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.summary-label {
  font-size: 13px;
  color: #94a3b8;
}

.summary-value {
  font-size: 15px;
  font-weight: 700;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
}

.summary-divider {
  height: 1px;
  background: #2a2a3a;
  margin: 4px 0;
}

.full-width {
  width: 100%;
  margin-top: 4px;
}

/* Quick Actions */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: #12121a;
  border: 1px solid #2a2a3a;
  border-radius: 10px;
  color: #e2e8f0;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  width: 100%;
  text-align: left;
}

.action-btn:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: rgba(99, 102, 241, 0.05);
}

.action-btn svg {
  opacity: 0.7;
}

.action-btn:hover svg {
  opacity: 1;
}

/* Responsive */
@media (max-width: 1200px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }

  .sidebar-section {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-item {
    padding: 16px;
  }

  .stat-number {
    font-size: 18px;
  }

  .sidebar-section {
    grid-template-columns: 1fr;
  }

  .welcome-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-container {
    padding: 12px;
  }

  .content-card {
    padding: 16px;
  }
}
</style>
