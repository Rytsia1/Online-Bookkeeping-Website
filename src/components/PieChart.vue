<template>
  <div ref="chartContainer" class="pie-chart-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts/core'
import { PieChart as PieChartSeries } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// Register only the components we need
echarts.use([PieChartSeries, TitleComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
  title: {
    type: String,
    default: 'Distribusi Pengeluaran',
  },
})

const chartContainer = ref(null)
let chartInstance = null

// Initialize chart
const initChart = () => {
  if (!chartContainer.value) return

  chartInstance = echarts.init(chartContainer.value)

  const option = {
    title: {
      text: props.title,
      left: 'center',
      top: 20,
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333',
      },
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
    },
    series: [
      {
        name: 'Kategori',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['45%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold',
          },
        },
        labelLine: {
          show: false,
        },
        data: props.data.length
          ? props.data
          : [
              {
                value: 0,
                name: 'Tidak ada data',
              },
            ],
      },
    ],
  }

  chartInstance.setOption(option)
}

// Debounced resize handler to avoid excessive redraws
let resizeTimer = null
const handleResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    if (chartInstance) {
      chartInstance.resize()
    }
  }, 200)
}

// Watch for data changes
watch(
  () => props.data,
  () => {
    if (chartInstance) {
      const newOption = {
        series: [
          {
            data: props.data.length
              ? props.data
              : [
                  {
                    value: 0,
                    name: 'Tidak ada data',
                  },
                ],
          },
        ],
      }
      chartInstance.setOption(newOption)
    }
  },
  { deep: true }
)

// Lifecycle hooks
onMounted(() => {
  initChart()

  // Add window resize listener for responsiveness
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  // Clean up: remove resize listener and dispose chart
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.pie-chart-container {
  width: 100%;
  height: 400px;
  min-height: 400px;
}
</style>
