<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div class="page-header__left">
        <p class="page-eyebrow">BOOKKEEPING / SAVINGS</p>
        <h1 class="page-title">Savings Goals</h1>
        <p class="page-date">Lacak target finansialmu dan sisihkan uang secara berkala</p>
      </div>
      <button class="btn-primary" @click="handleCreate">+ NEW GOAL</button>
    </div>

    <!-- Summary Bar -->
    <div class="summary-bar" v-if="goals.length > 0">
      <div class="summary-item">
        <p class="summary-label">TOTAL GOALS</p>
        <p class="summary-value">{{ goals.length }}</p>
      </div>
      <div class="summary-item">
        <p class="summary-label">COMPLETED</p>
        <p class="summary-value value--green">{{ completedCount }}</p>
      </div>
      <div class="summary-item">
        <p class="summary-label">TOTAL TARGET</p>
        <p class="summary-value mono">{{ formatCurrency(totalTarget) }}</p>
      </div>
      <div class="summary-item">
        <p class="summary-label">TOTAL SAVED</p>
        <p class="summary-value mono value--green">{{ formatCurrency(totalSaved) }}</p>
      </div>
    </div>

    <!-- Goals Grid -->
    <div v-if="goals.length > 0" class="goals-grid">
      <transition-group name="goal-list" tag="div" class="goals-grid-inner">
        <div
          v-for="goal in goals"
          :key="goal.id"
          :class="['goal-card', goal.completed ? 'goal-card--done' : '']"
        >
          <!-- Card Header -->
          <div class="goal-card__header">
            <div class="goal-card__title-group">
              <span class="goal-card__status-dot" :class="goal.completed ? 'dot--done' : 'dot--active'"></span>
              <h3 class="goal-card__name">{{ goal.name }}</h3>
            </div>
            <div class="goal-card__actions">
              <span v-if="goal.completed" class="badge badge--done">✓ DONE</span>
              <button class="icon-btn icon-btn--delete" @click="handleDelete(goal.id)" title="Delete">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"/>
                  <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Note -->
          <p v-if="goal.note" class="goal-card__note">{{ goal.note }}</p>

          <!-- Progress -->
          <div class="goal-card__progress-section">
            <div class="goal-card__amounts">
              <span class="amount-saved mono">{{ formatCurrency(goal.saved) }}</span>
              <span class="amount-separator">of</span>
              <span class="amount-target mono">{{ formatCurrency(goal.target) }}</span>
            </div>
            <div class="progress-track">
              <div
                class="progress-fill"
                :class="goal.completed ? 'progress-fill--done' : ''"
                :style="{ width: Math.min(getProgress(goal), 100) + '%' }"
              ></div>
            </div>
            <div class="progress-meta">
              <span :class="goal.completed ? 'text-green' : 'text-ash'">{{ getProgress(goal).toFixed(1) }}%</span>
              <span class="text-ash mono" v-if="!goal.completed">
                sisa {{ formatCurrency(Number(goal.target) - Number(goal.saved)) }}
              </span>
              <span class="text-green" v-else>Target tercapai! 🎉</span>
            </div>
          </div>

          <!-- Allocate Button -->
          <button
            v-if="!goal.completed"
            class="btn-allocate full-width"
            @click="openAllocateDialog(goal)"
          >
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            ALLOCATE SAVINGS
          </button>
        </div>
      </transition-group>
    </div>

    <!-- Empty State -->
    <div v-else class="empty-state">
      <div class="empty-state__icon">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z"/>
          <path d="M12 6v6l4 2"/>
        </svg>
      </div>
      <h2 class="empty-state__title">Belum ada target tabungan</h2>
      <p class="empty-state__desc">Buat target finansialmu dan pantau progresnya setiap saat.</p>
      <button class="btn-primary" @click="handleCreate">+ BUAT GOAL PERTAMA</button>
    </div>

    <!-- Create Goal Dialog -->
    <el-dialog
      v-model="createDialogVisible"
      title="SAVINGS GOAL BARU"
      width="460px"
      @close="resetCreateForm"
      class="forge-dialog"
    >
      <div class="dialog-body">
        <p class="dialog-desc">Tentukan nama target dan jumlah yang ingin dicapai.</p>
        <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-position="top">
          <el-form-item label="NAMA GOAL" prop="name">
            <el-input
              v-model="createForm.name"
              placeholder="cth: Beli Laptop Baru"
              size="large"
            />
          </el-form-item>
          <el-form-item label="TARGET JUMLAH (USD)" prop="target">
            <el-input-number
              v-model="createForm.target"
              :min="1"
              :step="100"
              :precision="0"
              style="width: 100%"
              size="large"
            />
          </el-form-item>
          <p v-if="createForm.target > 0" class="amount-preview">
            {{ formatCurrency(createForm.target) }}
          </p>
          <el-form-item label="CATATAN (opsional)">
            <el-input
              v-model="createForm.note"
              type="textarea"
              placeholder="Tambahkan catatan..."
              :rows="2"
              size="large"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="createDialogVisible = false">BATAL</el-button>
        <el-button type="primary" :loading="saving" @click="handleCreateSubmit">BUAT GOAL</el-button>
      </template>
    </el-dialog>

    <!-- Allocate Dialog -->
    <el-dialog
      v-model="allocateDialogVisible"
      title="ALOKASI TABUNGAN"
      width="420px"
      class="forge-dialog"
    >
      <div class="dialog-body" v-if="allocateTarget">
        <p class="dialog-desc">
          Tambahkan jumlah tabungan ke goal <strong>{{ allocateTarget.name }}</strong>.
          Progres saat ini: <span class="text-ember">{{ getProgress(allocateTarget).toFixed(1) }}%</span>
        </p>
        <el-form label-position="top">
          <el-form-item label="JUMLAH ALOKASI (USD)">
            <el-input-number
              v-model="allocateAmount"
              :min="1"
              :max="Number(allocateTarget.target) - Number(allocateTarget.saved)"
              :step="50"
              :precision="0"
              style="width: 100%"
              size="large"
            />
          </el-form-item>
          <div v-if="allocateAmount > 0" class="allocate-preview">
            <div class="allocate-preview__row">
              <span class="text-ash">Sebelum</span>
              <span class="mono">{{ formatCurrency(allocateTarget.saved) }}</span>
            </div>
            <div class="allocate-preview__row">
              <span class="text-ash">Tambah</span>
              <span class="mono text-green">+ {{ formatCurrency(allocateAmount) }}</span>
            </div>
            <div class="allocate-preview__divider"></div>
            <div class="allocate-preview__row">
              <span class="text-ash">Total baru</span>
              <span class="mono fw-700">{{ formatCurrency(Number(allocateTarget.saved) + allocateAmount) }}</span>
            </div>
          </div>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="allocateDialogVisible = false">BATAL</el-button>
        <el-button type="primary" :loading="saving" @click="handleAllocateSubmit">SIMPAN</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// ── State ──
const goals   = ref([])
const saving  = ref(false)

// Create dialog
const createDialogVisible = ref(false)
const createFormRef       = ref(null)
const createForm          = ref({ name: '', target: 1000, note: '' })

const createRules = {
  name:   [{ required: true, message: 'Nama goal wajib diisi', trigger: 'blur' }],
  target: [{ required: true, type: 'number', min: 1, message: 'Target harus lebih dari 0', trigger: 'blur' }],
}

// Allocate dialog
const allocateDialogVisible = ref(false)
const allocateTarget        = ref(null)
const allocateAmount        = ref(100)

// ── Computed ──
const completedCount = computed(() => goals.value.filter(g => g.completed).length)
const totalTarget    = computed(() => goals.value.reduce((s, g) => s + Number(g.target), 0))
const totalSaved     = computed(() => goals.value.reduce((s, g) => s + Number(g.saved), 0))

const getProgress = (goal) => {
  const t = Number(goal.target)
  if (!t) return 0
  return Math.min((Number(goal.saved) / t) * 100, 100)
}

// ── Helpers ──
const formatCurrency = (amount) => {
  if (amount == null) return '$ 0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency', currency: 'USD',
    minimumFractionDigits: 0, maximumFractionDigits: 0,
  }).format(amount)
}

// ── API ──
const fetchGoals = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const data = await request.get('/saving-goals', { params: { userId } })
    goals.value = Array.isArray(data) ? data : []
  } catch (e) {
    ElMessage.error('Gagal memuat savings goals')
  }
}

// ── Handlers ──
const handleCreate = () => {
  resetCreateForm()
  createDialogVisible.value = true
}

const resetCreateForm = () => {
  createForm.value = { name: '', target: 1000, note: '' }
  createFormRef.value?.clearValidate()
}

const handleCreateSubmit = async () => {
  if (!createFormRef.value) return
  try {
    await createFormRef.value.validate()
    saving.value = true
    const userId = Number(localStorage.getItem('userId'))
    await request.post('/saving-goals', {
      userId,
      name:   createForm.value.name,
      target: createForm.value.target,
      note:   createForm.value.note || null,
    })
    ElMessage.success('Goal berhasil dibuat!')
    createDialogVisible.value = false
    await fetchGoals()
  } catch (e) {
    if (e?.message) ElMessage.error('Gagal membuat goal')
  } finally {
    saving.value = false
  }
}

const openAllocateDialog = (goal) => {
  allocateTarget.value = goal
  allocateAmount.value = 100
  allocateDialogVisible.value = true
}

const handleAllocateSubmit = async () => {
  if (!allocateAmount.value || allocateAmount.value <= 0) {
    ElMessage.warning('Masukkan jumlah yang valid')
    return
  }
  try {
    saving.value = true
    const newSaved = Number(allocateTarget.value.saved) + allocateAmount.value
    await request.put(`/saving-goals/${allocateTarget.value.id}/allocate`, { newSaved })
    ElMessage.success('Tabungan berhasil dialokasikan!')
    allocateDialogVisible.value = false
    await fetchGoals()
  } catch (e) {
    ElMessage.error('Gagal mengalokasikan tabungan')
  } finally {
    saving.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('Hapus saving goal ini?', 'Konfirmasi', {
    confirmButtonText: 'HAPUS', cancelButtonText: 'Batal', type: 'warning',
  }).then(async () => {
    try {
      await request.delete(`/saving-goals/${id}`)
      ElMessage.success('Goal dihapus')
      await fetchGoals()
    } catch (e) {
      ElMessage.error('Gagal menghapus')
    }
  }).catch(() => {})
}

onMounted(fetchGoals)
</script>

<style scoped>
/* ── Layout ── */
.page {
  padding: 28px 32px;
  max-width: 1440px;
  margin: 0 auto;
  min-height: 100vh;
}

/* ── Page Header ── */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--wire);
}

.page-eyebrow {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--ash);
  margin-bottom: 6px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--white);
  line-height: 1.1;
}

.page-date {
  font-size: 12px;
  color: var(--ash);
  margin-top: 4px;
}

/* ── Summary Bar ── */
.summary-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
  background: var(--wire);
  border: 1px solid var(--wire);
  border-radius: 3px;
  margin-bottom: 28px;
  overflow: hidden;
}

.summary-item {
  padding: 18px 22px;
  background: var(--graphite);
}

.summary-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1.5px;
  color: var(--ash);
  margin-bottom: 8px;
}

.summary-value {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--white);
  line-height: 1;
}

/* ── Goals Grid ── */
.goals-grid { }

.goals-grid-inner {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

/* ── Goal Card ── */
.goal-card {
  background: var(--graphite);
  border: 1px solid var(--wire);
  border-radius: 6px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.goal-card:hover {
  border-color: rgba(240, 90, 20, 0.35);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.goal-card--done {
  border-color: rgba(34, 197, 94, 0.3);
  background: rgba(34, 197, 94, 0.03);
}

.goal-card--done:hover {
  border-color: rgba(34, 197, 94, 0.5);
}

.goal-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.goal-card__title-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.goal-card__status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot--active { background: var(--ember); box-shadow: 0 0 6px rgba(240,90,20,0.5); }
.dot--done   { background: var(--green); box-shadow: 0 0 6px rgba(34,197,94,0.5); }

.goal-card__name {
  font-size: 15px;
  font-weight: 700;
  color: var(--white);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

.goal-card__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.goal-card__note {
  font-size: 12px;
  color: var(--ash);
  line-height: 1.5;
  margin: 0;
  padding: 8px 12px;
  background: var(--ink);
  border-radius: 3px;
  border-left: 2px solid var(--wire);
}

/* ── Progress ── */
.goal-card__progress-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.goal-card__amounts {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.amount-saved {
  font-size: 22px;
  font-weight: 700;
  color: var(--white);
}

.amount-separator {
  font-size: 11px;
  color: var(--ash);
}

.amount-target {
  font-size: 13px;
  color: var(--muted);
}

.progress-track {
  height: 5px;
  background: var(--wire);
  border-radius: 99px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--ember), var(--spark));
  border-radius: 99px;
  transition: width 0.6s ease;
}

.progress-fill--done {
  background: linear-gradient(90deg, #16a34a, #22c55e);
}

.progress-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
}

/* ── Allocate Button ── */
.btn-allocate {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  padding: 9px 16px;
  background: transparent;
  border: 1px solid var(--wire);
  border-radius: 3px;
  color: var(--muted);
  font-size: 11px;
  font-weight: 700;
  font-family: var(--font-body);
  letter-spacing: 0.8px;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-allocate:hover {
  border-color: var(--ember);
  color: var(--ember);
  background: rgba(240, 90, 20, 0.06);
}

/* ── Primary Button ── */
.btn-primary {
  padding: 8px 16px;
  background: var(--ember);
  border: none;
  border-radius: 3px;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  font-family: var(--font-body);
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-primary:hover { background: var(--spark); }

/* ── Badges ── */
.badge {
  display: inline-block;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.8px;
  border-radius: 2px;
}
.badge--done { background: rgba(34, 197, 94, 0.12); color: var(--green); }

/* ── Icon Buttons ── */
.icon-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid var(--wire);
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.15s;
}

.icon-btn--delete { color: var(--ash); }
.icon-btn--delete:hover {
  border-color: var(--red);
  color: var(--red);
  background: rgba(239, 68, 68, 0.06);
}

/* ── Empty State ── */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
  gap: 16px;
}

.empty-state__icon {
  color: var(--wire);
  margin-bottom: 8px;
}

.empty-state__title {
  font-size: 20px;
  font-weight: 700;
  color: var(--muted);
}

.empty-state__desc {
  font-size: 13px;
  color: var(--ash);
  max-width: 340px;
  line-height: 1.6;
}

/* ── Dialog ── */
.dialog-body { display: flex; flex-direction: column; gap: 16px; }

.dialog-desc {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.6;
}

.amount-preview {
  font-family: var(--font-mono);
  font-size: 22px;
  font-weight: 600;
  color: var(--ember);
  margin: -8px 0 4px;
}

.allocate-preview {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px 16px;
  background: var(--ink);
  border-radius: 3px;
  border: 1px solid var(--wire);
  margin-top: 8px;
}

.allocate-preview__row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--bone);
}

.allocate-preview__divider {
  height: 1px;
  background: var(--wire);
}

/* ── Transitions ── */
.goal-list-enter-active { transition: all 0.3s ease; }
.goal-list-leave-active { transition: all 0.2s ease; }
.goal-list-enter-from   { opacity: 0; transform: translateY(10px); }
.goal-list-leave-to     { opacity: 0; transform: scale(0.95); }

/* ── Utilities ── */
.mono       { font-family: var(--font-mono) !important; }
.fw-700     { font-weight: 700 !important; }
.full-width { width: 100%; }
.text-green { color: var(--green) !important; }
.text-ash   { color: var(--ash) !important; }
.text-ember { color: var(--ember) !important; }
.value--green { color: var(--green) !important; }

/* ── Responsive ── */
@media (max-width: 1024px) {
  .summary-bar { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .page { padding: 16px; }
  .page-header { flex-direction: column; align-items: flex-start; gap: 16px; }
  .page-title { font-size: 22px; }
  .summary-bar { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 480px) {
  .page { padding: 12px; }
  .summary-bar { grid-template-columns: 1fr 1fr; }
  .goals-grid-inner { grid-template-columns: 1fr; }
}
</style>
