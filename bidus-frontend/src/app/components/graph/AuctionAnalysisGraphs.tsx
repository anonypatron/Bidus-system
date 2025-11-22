'use client';
import { AuctionAnalysisDto } from '../../../../types/dto/response/auction';
import { Line } from 'react-chartjs-2';
import {
  Chart,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ChartOptions,
  TooltipItem,
} from 'chart.js';

Chart.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface Props {
  auctionHistory: AuctionAnalysisDto[];
  colors: string[]; // ComparePage에서 받은 원본 COLORS
}

/**
 * [핵심 수정] 
 * X축을 '경과 시간(분)'이 아닌 '경매 진행률(%)'로 정규화합니다.
 */
function processChartData(history: AuctionAnalysisDto[], colors: string[]) {
  
  const datasets = history.map((auction, index) => {
    const startTimeMs = new Date(auction.startTime).getTime();
    const endTimeMs = new Date(auction.endTime).getTime();
    
    // [핵심] 0으로 나누는 것을 방지하기 위해 최소 1분으로 설정
    const totalDurationMinutes = Math.max(1, (endTimeMs - startTimeMs) / (1000 * 60));

    let currentHighestPrice = auction.startPrice;
    const data: { x: number, y: number }[] = [];

    // 1. 0% 지점 (시작 가격) 추가
    data.push({ x: 0, y: currentHighestPrice });

    // 2. 입찰 기록을 시간순으로 정렬
    const sortedBids = [...auction.bidHistoryGraph].sort((a, b) => 
        new Date(a.time).getTime() - new Date(b.time).getTime()
    );

    // 3. 정렬된 입찰 기록을 순회하며 "최고가" 갱신
    sortedBids.forEach(point => {
      const pointTimeMs = new Date(point.time).getTime();
      const elapsedMinutes = Math.max(0, (pointTimeMs - startTimeMs)) / (1000 * 60);

      // [핵심] X축 값을 '경과 시간'이 아닌 '진행률 %'로 변환
      const x_percentage = (elapsedMinutes / totalDurationMinutes) * 100;

      if (point.price > currentHighestPrice) {
        // 계단식 그래프를 위해, 갱신 직전 %에 이전 가격을 찍음
        data.push({ x: x_percentage - 0.0001, y: currentHighestPrice });
        currentHighestPrice = point.price;
        data.push({ x: x_percentage, y: currentHighestPrice });
      }
    });

    // 4. 100% 지점 (경매 종료) 추가
    data.push({ x: 100, y: currentHighestPrice });

    // 5. 데이터셋 반환
    return {
      label: auction.title,
      data: data,
      borderColor: colors[index % colors.length],
      backgroundColor: colors[index % colors.length] + '33',
      fill: false,
      stepped: true,
      pointRadius: 1,
      pointHoverRadius: 5,
    };
  });

  return { datasets };
}

// [핵심 수정] X축을 0~100% 스케일로 변경
const chartOptions: ChartOptions<'line'> = {
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    mode: 'index',
    intersect: false,
  },
  plugins: {
    legend: {
      position: 'top' as const,
    },
    tooltip: {
      callbacks: {
        title: function(tooltipItems: TooltipItem<'line'>[]) {
          const x = tooltipItems[0].parsed.x;
          // 툴팁에는 진행률(%) 표시
          return `경매 진행률: ${x?.toFixed(1)} %`; 
        },
        label: function (context: TooltipItem<'line'>) {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          if (context.parsed.y !== null) {
            label += `${context.parsed.y.toLocaleString()} 원`;
          }
          return label;
        },
      },
    },
  },
  scales: {
    x: {
      type: 'linear', 
      title: {
        display: true,
        text: '경매 진행률 (%)', // [수정] X축 제목
      },
      // [수정] X축을 0% ~ 100%로 고정
      min: 0,
      max: 100,
      ticks: {
        // [수정] X축 눈금에 "%" 추가
        callback: (value: any) => `${value}%`,
      },
    },
    y: {
      type: 'linear',
      title: {
        display: true,
        text: '가격 (원)',
      },
      ticks: {
        callback: (value: any) => {
          if (Number(value) >= 1000000) return `${(Number(value) / 1000000).toFixed(1)}M`;
          if (Number(value) >= 1000) return `${(Number(value) / 1000).toFixed(0)}K`;
          return value;
        },
      },
    },
  },
};

export default function AuctionAnalysisGraphs({ auctionHistory, colors}: Props) {
  const { datasets } = processChartData(auctionHistory, colors);

  return (
    <div className="chart-container" style={{ minHeight: '500px', position: 'relative' }}>
      <Line options={chartOptions} data={{ datasets }} />
    </div>
  );
};