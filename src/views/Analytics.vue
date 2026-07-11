<template>
  <div class="analytics-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">Analitik Keuangan</h1>
        <p class="page-subtitle">Ringkasan dan visualisasi data keuangan Anda</p>
      </div>
      <button class="refresh-btn" @click="refreshAnalytics">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <polyline points="23 4 23 10 17 10" />
          <path d="M20.49 15a9 9 0 11-2.12-9.36L23 10" />
        </svg>
        Refresh Data
      </button>
    </div>

    <!-- Summary Cards -->
    <div class="summary-section">
      <div class="stat-grid">
        <div class="stat-card">
          <div class="stat-icon income-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="12" y1="19" x2="12" y2="5" />
              <polyline points="5 12 12 5 19 12" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">Total Pemasukan</span>
            <span class="stat-value income-value">{{ formatCurrency(stats.income) }}</span>
            <span class="stat-trend">
              <span class="trend-badge income-badge">{{ incomePercentage }}%</span>
              dari total transaksi
            </span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon expense-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="12" y1="5" x2="12" y2="19" />
              <polyline points="19 12 12 19 5 12" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">Total Pengeluaran</span>
            <span class="stat-value expense-value">{{ formatCurrency(stats.expense) }}</span>
            <span class="stat-trend">
              <span class="trend-badge expense-badge">{{ expensePercentage }}%</span>
              dari total transaksi
            </span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon balance-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="12" y1="1" x2="12" y2="23" />
              <path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">Saldo Tersisa</span>
            <span class="stat-value" :class="stats.balance >= 0 ? 'balance-positive' : 'balance-negative'">
              {{ formatCurrency(stats.balance) }}
            </span>
            <span class="stat-trend">
              <span :class="['trend-badge', stats.balance >= 0 ? 'surplus-badge' : 'deficit-badge']">
                {{ stats.balance >= 0 ? 'Surplus' : 'Defisit' }}
              </span>
              status keuangan
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-section">
      <div class="charts-grid">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">Distribusi Pengeluaran</h3>
            <span class="chart-subtitle">Berdasarkan kategori</span>
          </div>
          <div class="chart-body">
            <PieChart :data="categoryData" title="Pengeluaran per Kategori" />
          </div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">Ringkasan Transaksi</h3>
            <span class="chart-subtitle">Pemasukan vs Pengeluaran</span>
          </div>
          <div class="chart-body">
            <PieChart :data="typeData" title="Pemasukan vs Pengeluaran" />
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!hasData" class="empty-state">
      <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#475569" stroke-width="1.5">
        <path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        <path d="M9 10h.01M15 10h.01M9.5 15.5a3.5 3.5 0 015 0" />
      </svg>
      <h3 class="empty-title">Belum Ada Data</h3>
      <p class="empty-text">Mulai tambahkan tagihan untuk melihat analitik keuangan Anda</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PieChart from '@/components/PieChart.vue'
import request from '@/utils/request'

// State
const stats = ref({
  income: 0,
  expense: 0,
  balance: 0,
})

const categoryData = ref([])
const typeData = ref([])
const loading = ref(false)

// Computed Properties
const hasData = computed(() => stats.value.income > 0 || stats.value.expense > 0)

const incomePercentage = computed(() => {
  const total = stats.value.income + stats.value.expense
  return total > 0 ? Math.round((stats.value.income / total) * 100) : 0
})

const expensePercentage = computed(() => {
  const total = stats.value.income + stats.value.expense
  return total > 0 ? Math.round((stats.value.expense / total) * 100) : 0
})

// Methods
const formatCurrency = (amount) => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0,
  }).format(amount)
}

const fetchSummary = async () => {
  try {
    loading.value = true
    const data = await request.get('/stats/summary')
    stats.value = {
      income: data.income || 0,
      expense: data.expense || 0,
      balance: (data.balance || 0),
    }
  } catch (error) {
    ElMessage.error('Gagal memuat ringkasan statistik')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCategoryData = async () => {
  try {
    const data = await request.get('/stats/categories')
    categoryData.value = Array.isArray(data)
      ? data.map((item) => ({
          name: item.name || item.category,
          value: item.value || item.amount,
        }))
      : []
  } catch (error) {
    ElMessage.error('Gagal memuat data kategori')
    console.error(error)
    categoryData.value = []
  }
}

const fetchTypeData = async () => {
  try {
    const data = [
      { name: 'Pemasukan', value: stats.value.income },
      { name: 'Pengeluaran', value: stats.value.expense },
    ]
    typeData.value = data.filter((item) => item.value > 0)
  } catch (error) {
    console.error(error)
  }
}

const fetchAnalytics = async () => {
  await Promise.all([fetchSummary(), fetchCategoryData()])
  fetchTypeData()
}

const refreshAnalytics = async () => {
  ElMessage.info('Memperbarui data...')
  await fetchAnalytics()
  ElMessage.success('Data berhasil diperbarui')
}

// Lifecycle
onMounted(() => {
  fetchAnalytics()
})

defineExpose({
  refreshAnalytics,
})
</script>

<style scoped>
.analytics-container {
  padding: 32px;
  max-width: 1440px;
  margin: 0 auto;
  min-height: 100vh;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #f1f5f9;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 4px 0 0 0;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 10px;
  color: #94a3b8;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: rgba(99, 102, 241, 0.05);
}

/* Summary Cards */
.summary-section {
  margin-bottom: 32px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 14px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  border-color: #3a3a4a;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.income-icon {
  background: rgba(16, 185, 129, 0.12);
  color: #10b981;
}

.expense-icon {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}

.balance-icon {
  background: rgba(99, 102, 241, 0.12);
  color: #6366f1;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
}

.income-value {
  color: #10b981;
}

.expense-value {
  color: #ef4444;
}

.balance-positive {
  color: #10b981;
}

.balance-negative {
  color: #ef4444;
}

.stat-trend {
  font-size: 12px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.trend-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}

.income-badge {
  background: rgba(16, 185, 129, 0.12);
  color: #10b981;
}

.expense-badge {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}

.surplus-badge {
  background: rgba(16, 185, 129, 0.12);
  color: #10b981;
}

.deficit-badge {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}

/* Charts Section */
.charts-section {
  margin-bottom: 32px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 14px;
  overflow: hidden;
}

.chart-header {
  padding: 20px 24px 0;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0;
}

.chart-subtitle {
  font-size: 13px;
  color: #64748b;
}

.chart-body {
  padding: 16px 24px 24px;
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #94a3b8;
  margin: 20px 0 8px;
}

.empty-text {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-grid .stat-card:last-child {
    grid-column: 1 / -1;
  }
}

@media (max-width: 768px) {
  .analytics-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .stat-grid {
    grid-template-columns: 1fr;
  }

  .stat-grid .stat-card:last-child {
    grid-column: auto;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 20px;
  }

  .stat-value {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .analytics-container {
    padding: 12px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
  }

  .stat-value {
    font-size: 18px;
  }
}
</style>
