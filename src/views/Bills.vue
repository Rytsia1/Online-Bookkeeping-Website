<template>
  <div class="bills-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">Manajemen Tagihan</h1>
        <p class="page-subtitle">Kelola semua transaksi pemasukan dan pengeluaran Anda</p>
      </div>
      <el-button type="primary" class="btn-primary-custom" @click="handleCreate">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="margin-right: 6px">
          <line x1="12" y1="5" x2="12" y2="19" /><line x1="5" y1="12" x2="19" y2="12" />
        </svg>
        Tambah Tagihan
      </el-button>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <button
        v-for="filter in filters"
        :key="filter.value"
        :class="['filter-btn', { active: activeFilter === filter.value }]"
        @click="activeFilter = filter.value"
      >
        {{ filter.label }}
        <span v-if="getFilterCount(filter.value)" class="filter-count">{{ getFilterCount(filter.value) }}</span>
      </button>
    </div>

    <!-- Bills Table -->
    <div class="table-card">
      <div class="table-wrapper">
        <el-table
          :data="filteredBills"
          stripe
          style="width: 100%"
          :header-cell-style="tableHeaderStyle"
          :cell-style="tableCellStyle"
          :default-sort="{ prop: 'date', order: 'descending' }"
        >
          <el-table-column prop="date" label="Tanggal" width="120" sortable />
          <el-table-column prop="type" label="Tipe" width="130">
            <template #default="{ row }">
              <span :class="['type-badge', row.type === 'income' ? 'badge-income' : 'badge-expense']">
                {{ row.type === 'income' ? 'Pemasukan' : 'Pengeluaran' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="Kategori" width="140">
            <template #default="{ row }">
              <span class="category-text">{{ row.category }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="Jumlah" width="160">
            <template #default="{ row }">
              <span :class="['amount-text', row.type === 'income' ? 'income-text' : 'expense-text']">
                {{ row.type === 'income' ? '+' : '-' }}{{ formatCurrency(row.amount) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="Deskripsi" min-width="200" show-overflow-tooltip />
          <el-table-column label="Aksi" width="160" fixed="right">
            <template #default="{ row }">
              <div class="action-cell">
                <button class="action-icon-btn edit-btn" @click="handleEdit(row)" title="Edit">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
                    <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
                  </svg>
                </button>
                <button class="action-icon-btn delete-btn" @click="handleDelete(row.id)" title="Hapus">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6" />
                    <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" />
                  </svg>
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-empty v-if="filteredBills.length === 0" description="Tidak ada tagihan ditemukan" class="empty-state" />
    </div>

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? 'Tambah Tagihan Baru' : 'Edit Tagihan'"
      width="500px"
      class="dark-dialog"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-position="top" class="dark-form">
        <el-form-item label="Tipe Transaksi" prop="type">
          <el-select v-model="formData.type" placeholder="Pilih tipe" size="large" style="width: 100%">
            <el-option label="Pemasukan" value="income" />
            <el-option label="Pengeluaran" value="expense" />
          </el-select>
        </el-form-item>

        <el-form-item label="Kategori" prop="category">
          <el-input v-model="formData.category" placeholder="Contoh: Makanan, Transport, Gaji" size="large" />
        </el-form-item>

        <el-form-item label="Jumlah (IDR)" prop="amount">
          <el-input-number
            v-model="formData.amount"
            :min="0"
            :step="10000"
            placeholder="Masukkan jumlah"
            style="width: 100%"
            size="large"
          />
        </el-form-item>

        <el-form-item label="Tanggal" prop="date">
          <el-date-picker
            v-model="formData.date"
            type="date"
            placeholder="Pilih tanggal"
            style="width: 100%"
            size="large"
          />
        </el-form-item>

        <el-form-item label="Deskripsi" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="Tambahkan catatan untuk tagihan ini"
            rows="3"
            size="large"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="btn-outline-custom" @click="dialogVisible = false">Batal</el-button>
          <el-button type="primary" class="btn-primary-custom" @click="handleSubmit">Simpan</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// State
const bills = ref([])
const activeFilter = ref('all')
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref(null)
const loading = ref(false)

const filters = [
  { label: 'Semua', value: 'all' },
  { label: 'Pemasukan', value: 'income' },
  { label: 'Pengeluaran', value: 'expense' },
]

const formData = ref({
  type: 'expense',
  category: '',
  amount: 0,
  date: new Date().toISOString().split('T')[0],
  description: '',
})

const rules = {
  type: [{ required: true, message: 'Tipe wajib dipilih', trigger: 'change' }],
  category: [{ required: true, message: 'Kategori wajib diisi', trigger: 'blur' }],
  amount: [{ required: true, message: 'Jumlah wajib diisi', trigger: 'blur' }],
  date: [{ required: true, message: 'Tanggal wajib dipilih', trigger: 'change' }],
}

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
const filteredBills = computed(() => {
  if (activeFilter.value === 'all') {
    return bills.value
  }
  return bills.value.filter((bill) => bill.type === activeFilter.value)
})

const getFilterCount = (filterValue) => {
  if (filterValue === 'all') return bills.value.length
  return bills.value.filter((bill) => bill.type === filterValue).length
}

// Methods
const formatCurrency = (amount) => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0,
  }).format(amount)
}

const fetchBills = async () => {
  try {
    loading.value = true
    const data = await request.get('/bills')
    bills.value = data.sort((a, b) => new Date(b.date) - new Date(a.date))
  } catch (error) {
    ElMessage.error('Gagal memuat data tagihan')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogMode.value = 'edit'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('Apakah Anda yakin ingin menghapus tagihan ini?', 'Konfirmasi Hapus', {
    confirmButtonText: 'Hapus',
    cancelButtonText: 'Batal',
    type: 'warning',
  })
    .then(async () => {
      try {
        await request.delete(`/bills/${id}`)
        ElMessage.success('Tagihan berhasil dihapus')
        fetchBills()
      } catch (error) {
        ElMessage.error('Gagal menghapus tagihan')
        console.error(error)
      }
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    if (dialogMode.value === 'create') {
      await request.post('/bills', formData.value)
      ElMessage.success('Tagihan berhasil ditambahkan')
    } else {
      await request.put(`/bills/${formData.value.id}`, formData.value)
      ElMessage.success('Tagihan berhasil diperbarui')
    }

    dialogVisible.value = false
    fetchBills()
  } catch (error) {
    ElMessage.error('Gagal menyimpan tagihan')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    type: 'expense',
    category: '',
    amount: 0,
    date: new Date().toISOString().split('T')[0],
    description: '',
  }
  formRef.value?.clearValidate()
}

// Lifecycle
onMounted(() => {
  fetchBills()
})
</script>

<style scoped>
.bills-container {
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
  margin-bottom: 28px;
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

.btn-primary-custom {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  border-radius: 10px;
  font-weight: 600;
  padding: 10px 20px;
  display: flex;
  align-items: center;
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
  transition: all 0.2s ease;
}

.btn-outline-custom:hover {
  border-color: #6366f1;
  color: #6366f1;
}

/* Filter Section */
.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 8px;
  color: #94a3b8;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-btn:hover {
  border-color: #6366f1;
  color: #e2e8f0;
}

.filter-btn.active {
  background: rgba(99, 102, 241, 0.12);
  border-color: #6366f1;
  color: #6366f1;
}

.filter-count {
  background: #2a2a3a;
  color: #94a3b8;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.filter-btn.active .filter-count {
  background: rgba(99, 102, 241, 0.2);
  color: #6366f1;
}

/* Table Card */
.table-card {
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 14px;
  padding: 24px;
}

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

.table-wrapper :deep(.el-table__empty-block) {
  background: #1a1a24;
}

/* Badge & Text Styles */
.type-badge {
  display: inline-block;
  padding: 4px 12px;
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

.category-text {
  color: #cbd5e1;
  font-weight: 500;
}

.amount-text {
  font-weight: 700;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 13px;
}

.income-text {
  color: #10b981;
}

.expense-text {
  color: #ef4444;
}

/* Action Cell */
.action-cell {
  display: flex;
  gap: 8px;
}

.action-icon-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid #2a2a3a;
  background: #12121a;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-btn {
  color: #6366f1;
}

.edit-btn:hover {
  background: rgba(99, 102, 241, 0.1);
  border-color: #6366f1;
}

.delete-btn {
  color: #ef4444;
}

.delete-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: #ef4444;
}

/* Empty State */
.empty-state {
  padding: 60px 0;
}

.empty-state :deep(.el-empty__description p) {
  color: #64748b;
}

/* Dialog Styles */
.dark-dialog :deep(.el-dialog) {
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 16px;
}

.dark-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #2a2a3a;
  padding: 20px 24px;
}

.dark-dialog :deep(.el-dialog__title) {
  color: #f1f5f9;
  font-weight: 600;
  font-size: 18px;
}

.dark-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.dark-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid #2a2a3a;
  padding: 16px 24px;
}

.dark-form :deep(.el-form-item__label) {
  color: #94a3b8;
  font-weight: 500;
  font-size: 13px;
}

.dark-form :deep(.el-input__wrapper),
.dark-form :deep(.el-textarea__inner) {
  background: #12121a;
  border: 1px solid #2a2a3a;
  border-radius: 10px;
  box-shadow: none;
  color: #f1f5f9;
}

.dark-form :deep(.el-input__wrapper:hover),
.dark-form :deep(.el-textarea__inner:hover) {
  border-color: #6366f1;
}

.dark-form :deep(.el-input__wrapper.is-focus),
.dark-form :deep(.el-textarea__inner:focus) {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.dark-form :deep(.el-input__inner),
.dark-form :deep(.el-textarea__inner) {
  color: #f1f5f9;
}

.dark-form :deep(.el-input__inner::placeholder),
.dark-form :deep(.el-textarea__inner::placeholder) {
  color: #475569;
}

.dark-form :deep(.el-select .el-input__wrapper) {
  background: #12121a;
}

.dark-form :deep(.el-input-number) {
  width: 100%;
}

.dark-form :deep(.el-input-number .el-input__wrapper) {
  background: #12121a;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* Responsive */
@media (max-width: 768px) {
  .bills-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .filter-section {
    flex-wrap: wrap;
  }

  .table-card {
    padding: 12px;
  }

  .page-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .bills-container {
    padding: 12px;
  }

  .filter-btn {
    padding: 6px 12px;
    font-size: 13px;
  }
}
</style>
