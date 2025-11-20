import { useState, useEffect, useRef } from 'react';
import { Plus, Trash2, Send, Package, Clock, CheckCircle2, XCircle, AlertCircle, Loader2, Shirt, Truck, Archive } from 'lucide-react';

interface ClothingProduct {
  name: string;
  code: number;
}

const CLOTHING_PRODUCTS: ClothingProduct[] = [
  { name: 'T-Shirt', code: 1000 },
  { name: 'Jeans', code: 1001 },
  { name: 'Hoodie', code: 1002 },
  { name: 'Jacket', code: 1003 },
  { name: 'Sweater', code: 1004 },
  { name: 'Dress Shirt', code: 1005 },
  { name: 'Polo Shirt', code: 1006 },
  { name: 'Shorts', code: 1007 },
  { name: 'Sweatpants', code: 1008 },
  { name: 'Dress Pants', code: 1009 },
  { name: 'Blazer', code: 1010 },
  { name: 'Cardigan', code: 1011 },
  { name: 'Tank Top', code: 1012 },
  { name: 'Skirt', code: 1013 },
  { name: 'Dress', code: 1014 }
];

interface OrderItem {
  productName: string;
  productCode: number;
  quantity: number;
  itemStatus?: string;
  fulfilledByWarehouseId?: number;
}

interface Order {
  customerId: number;
  deliveryAddress: string;
  items: { productCode: number; quantity: number }[];
}

interface OrderResponse {
  id: number;
  customerId: number;
  deliveryAddress: string;
  status: string;
  items: OrderItem[];
}


function App() {
  const [customerId, setCustomerId] = useState<number>(102);
  const [deliveryAddress, setDeliveryAddress] = useState<string>('456 Pine St, Vancouver, BC V6B 1B1');
  const [items, setItems] = useState<OrderItem[]>([
    { productName: 'T-Shirt', productCode: 1000, quantity: 1 }
  ]);
  const [apiUrl, setApiUrl] = useState<string>('http://localhost:8082/orders');
  const [currentOrder, setCurrentOrder] = useState<OrderResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [polling, setPolling] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const pollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const addItem = () => {
    setItems([...items, { productName: 'T-Shirt', productCode: 1000, quantity: 1 }]);
  };

  const removeItem = (index: number) => {
    if (items.length > 1) {
      setItems(items.filter((_, i) => i !== index));
    }
  };

  const updateItemProduct = (index: number, productName: string) => {
    const product = CLOTHING_PRODUCTS.find(p => p.name === productName);
    if (product) {
      const newItems = [...items];
      newItems[index].productName = product.name;
      newItems[index].productCode = product.code;
      setItems(newItems);
    }
  };

  const updateItemQuantity = (index: number, quantity: number) => {
    const newItems = [...items];
    newItems[index].quantity = quantity;
    setItems(newItems);
  };

  const fetchOrderStatus = async (orderId: number) => {
    try {
      const res = await fetch(`${apiUrl}/${orderId}`);
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${res.statusText}`);
      }
      const data: OrderResponse = await res.json();
      setCurrentOrder(data);

      const terminalStatuses = ['SCHEDULED_FOR_DELIVERY', 'FAILED'];
      if (terminalStatuses.includes(data.status)) {
        stopPolling();
      }
    } catch (err) {
      console.error('Error fetching order status:', err);
    }
  };

  const stopPolling = () => {
    if (pollingIntervalRef.current) {
      clearInterval(pollingIntervalRef.current);
      pollingIntervalRef.current = null;
      setPolling(false);
    }
  };

  const startPolling = (orderId: number) => {
    setPolling(true);
    fetchOrderStatus(orderId);
    pollingIntervalRef.current = setInterval(() => {
      fetchOrderStatus(orderId);
    }, 2000);
  };

  useEffect(() => {
    return () => {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
      }
    };
  }, []);

  const submitOrder = async () => {
    setLoading(true);
    setError('');
    setCurrentOrder(null);
    stopPolling();

    const order: Order = {
      customerId,
      deliveryAddress,
      items: items.map(item => ({
        productCode: item.productCode,
        quantity: item.quantity
      }))
    };

    try {
      const res = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(order)
      });

      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${res.statusText}`);
      }

      const data: OrderResponse = await res.json();
      setCurrentOrder(data);

      if (data.id) {
        startPolling(data.id);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to submit order');
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string, size: string = 'w-5 h-5') => {
    switch (status) {
      case 'RECEIVED':
        return <Clock className={`${size} text-blue-600`} />;
      case 'STOCK_RESERVED':
        return <Archive className={`${size} text-green-600`} />;
      case 'SCHEDULED_FOR_DELIVERY':
        return <Truck className={`${size} text-green-600`} />;
      case 'FAILED':
        return <XCircle className={`${size} text-red-600`} />;
      default:
        return <AlertCircle className={`${size} text-gray-600`} />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'RECEIVED':
        return 'bg-blue-50 border-blue-200 text-blue-800';
      case 'STOCK_RESERVED':
        return 'bg-green-50 border-green-200 text-green-800';
      case 'SCHEDULED_FOR_DELIVERY':
        return 'bg-green-50 border-green-200 text-green-800';
      case 'FAILED':
        return 'bg-red-50 border-red-200 text-red-800';
      default:
        return 'bg-gray-50 border-gray-200 text-gray-800';
    }
  };

  const getItemStatusColor = (status?: string) => {
    switch (status) {
      case 'RESERVED':
        return 'bg-green-100 text-green-800 border-green-300';
      case 'NOT_AVAILABLE':
        return 'bg-red-100 text-red-800 border-red-300';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  };

  const formatStatus = (status: string) => {
    return status.split('_').map(word =>
      word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
    ).join(' ');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      <div className="container mx-auto px-4 py-12 max-w-6xl">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white rounded-lg shadow-lg overflow-hidden h-fit">
            <div className="bg-slate-800 text-white px-8 py-6">
              <div className="flex items-center gap-3">
                <Package className="w-8 h-8" />
                <h1 className="text-3xl font-bold">Order Processing Simulator</h1>
              </div>
              <p className="text-slate-300 mt-2">Submit orders to your distribution system</p>
            </div>

            <div className="p-8 space-y-6">
              <div className="space-y-2">
                <label className="block text-sm font-semibold text-slate-700">
                  API Endpoint
                </label>
                <input
                  type="text"
                  value={apiUrl}
                  onChange={(e) => setApiUrl(e.target.value)}
                  className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                  placeholder="http://localhost:8082/orders"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-slate-700">
                    Customer ID
                  </label>
                  <input
                    type="number"
                    value={customerId}
                    onChange={(e) => setCustomerId(Number(e.target.value))}
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                  />
                </div>

                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-slate-700">
                    Delivery Address
                  </label>
                  <input
                    type="text"
                    value={deliveryAddress}
                    onChange={(e) => setDeliveryAddress(e.target.value)}
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                  />
                </div>
              </div>

              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <label className="block text-sm font-semibold text-slate-700">
                    Order Items
                  </label>
                  <button
                    onClick={addItem}
                    className="flex items-center gap-2 px-4 py-2 bg-slate-600 text-white rounded-lg hover:bg-slate-700 transition-colors"
                  >
                    <Plus className="w-4 h-4" />
                    Add Item
                  </button>
                </div>

                <div className="space-y-3">
                  {items.map((item, index) => (
                    <div key={index} className="flex gap-3 items-start p-4 bg-slate-50 rounded-lg border border-slate-200">
                      <div className="flex-1 grid grid-cols-1 md:grid-cols-3 gap-3">
                        <div className="md:col-span-2">
                          <label className="block text-xs font-medium text-slate-600 mb-1">
                            Product
                          </label>
                          <select
                            value={item.productName}
                            onChange={(e) => updateItemProduct(index, e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                          >
                            {CLOTHING_PRODUCTS.map(product => (
                              <option key={product.code} value={product.name}>
                                {product.name}
                              </option>
                            ))}
                          </select>
                        </div>
                        <div>
                          <label className="block text-xs font-medium text-slate-600 mb-1">
                            Code
                          </label>
                          <input
                            type="text"
                            value={item.productCode}
                            readOnly
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg bg-slate-100 text-slate-700 font-mono"
                          />
                        </div>
                        <div className="md:col-span-3">
                          <label className="block text-xs font-medium text-slate-600 mb-1">
                            Quantity
                          </label>
                          <input
                            type="number"
                            value={item.quantity}
                            onChange={(e) => updateItemQuantity(index, Number(e.target.value))}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                            min={1}
                          />
                        </div>
                      </div>
                      {items.length > 1 && (
                        <button
                          onClick={() => removeItem(index)}
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          title="Remove item"
                        >
                          <Trash2 className="w-5 h-5" />
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </div>

              <button
                onClick={submitOrder}
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-slate-800 text-white rounded-lg hover:bg-slate-900 disabled:bg-slate-400 disabled:cursor-not-allowed transition-colors font-semibold"
              >
                {loading ? (
                  <span>Processing...</span>
                ) : (
                  <>
                    <Send className="w-5 h-5" />
                    Submit Order
                  </>
                )}
              </button>

              {error && (
                <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                  <p className="text-red-800 font-medium">Error</p>
                  <p className="text-red-600 text-sm mt-1">{error}</p>
                </div>
              )}

              <div className="pt-6 border-t border-slate-200">
                <details>
                  <summary className="cursor-pointer font-semibold text-slate-700 hover:text-slate-900 flex items-center gap-2 mb-3">
                    <Shirt className="w-5 h-5" />
                    Product Code Reference
                  </summary>
                  <div className="overflow-x-auto">
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="border-b border-slate-200">
                          <th className="text-left py-2 px-3 font-semibold text-slate-700">Product</th>
                          <th className="text-left py-2 px-3 font-semibold text-slate-700">Code</th>
                        </tr>
                      </thead>
                      <tbody>
                        {CLOTHING_PRODUCTS.map(product => (
                          <tr key={product.code} className="border-b border-slate-100 hover:bg-slate-50">
                            <td className="py-2 px-3 text-slate-800">{product.name}</td>
                            <td className="py-2 px-3 font-mono text-slate-600">{product.code}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </details>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-lg overflow-hidden h-fit">
            <div className="bg-slate-700 text-white px-8 py-6">
              <h2 className="text-2xl font-bold">Order Status</h2>
              <p className="text-slate-300 mt-1">Real-time order tracking</p>
            </div>

            <div className="p-8">
              {!currentOrder && !error && (
                <div className="text-center py-12 text-slate-400">
                  <Package className="w-16 h-16 mx-auto mb-4 opacity-50" />
                  <p className="text-lg">No order submitted yet</p>
                  <p className="text-sm mt-2">Submit an order to see its status here</p>
                </div>
              )}

              {currentOrder && (
                <div className="space-y-6">
                  <div className={`p-4 rounded-lg border-2 ${getStatusColor(currentOrder.status)}`}>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        {getStatusIcon(currentOrder.status)}
                        <div>
                          <p className="text-sm font-medium">Order #{currentOrder.id}</p>
                          <p className="text-2xl font-bold mt-1">{formatStatus(currentOrder.status)}</p>
                        </div>
                      </div>
                      {polling && (
                        <Loader2 className="w-5 h-5 animate-spin text-slate-600" />
                      )}
                    </div>
                  </div>

                  <div>
                    <h3 className="text-lg font-semibold text-slate-800 mb-4">Order Progress</h3>
                    <div className="space-y-4">
                      {(() => {
                        const allStatuses = ['RECEIVED', 'STOCK_RESERVED', 'SCHEDULED_FOR_DELIVERY'];
                        const currentStatusIndex = allStatuses.indexOf(currentOrder.status);
                        const isFailed = currentOrder.status === 'FAILED';

                        if (isFailed) {
                          return (
                            <div className="bg-red-50 border-2 border-red-200 rounded-lg p-6 text-center">
                              <XCircle className="w-12 h-12 text-red-600 mx-auto mb-3" />
                              <p className="text-lg font-semibold text-red-800">Order Failed</p>
                              <p className="text-sm text-red-600 mt-1">Unable to process this order</p>
                            </div>
                          );
                        }

                        return (
                          <div className="space-y-6">
                            {allStatuses.map((status, index) => {
                              const isCompleted = index < currentStatusIndex;
                              const isCurrent = index === currentStatusIndex;
                              const isPending = index > currentStatusIndex;

                              return (
                                <div key={status}>
                                  <div className="flex items-center gap-4">
                                    <div className={`flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center border-2 transition-all ${
                                      isCompleted ? 'bg-green-100 border-green-500' :
                                      isCurrent ? 'bg-blue-100 border-blue-500' :
                                      'bg-slate-100 border-slate-300'
                                    }`}>
                                      {isCompleted ? (
                                        <CheckCircle2 className="w-6 h-6 text-green-600" />
                                      ) : isCurrent ? (
                                        polling ? (
                                          <Loader2 className="w-6 h-6 text-blue-600 animate-spin" />
                                        ) : (
                                          getStatusIcon(status, 'w-6 h-6')
                                        )
                                      ) : (
                                        <div className="w-3 h-3 rounded-full bg-slate-300" />
                                      )}
                                    </div>
                                    <div className="flex-1">
                                      <p className={`font-semibold ${
                                        isCompleted || isCurrent ? 'text-slate-800' : 'text-slate-400'
                                      }`}>
                                        {formatStatus(status)}
                                      </p>
                                      {isCurrent && (
                                        <p className="text-sm text-blue-600 font-medium mt-1">
                                          {polling ? 'In Progress...' : 'Current Status'}
                                        </p>
                                      )}
                                      {isCompleted && (
                                        <p className="text-sm text-green-600 mt-1">Completed</p>
                                      )}
                                    </div>
                                  </div>
                                  {index < allStatuses.length - 1 && (
                                    <div className="ml-5 mt-2 mb-2">
                                      <div className={`w-0.5 h-8 ${
                                        isCompleted ? 'bg-green-500' : 'bg-slate-300'
                                      }`} />
                                    </div>
                                  )}
                                </div>
                              );
                            })}
                          </div>
                        );
                      })()}
                    </div>
                  </div>

                  <div>
                    <h3 className="text-lg font-semibold text-slate-800 mb-3">Order Details</h3>
                    <div className="bg-slate-50 rounded-lg p-4 space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span className="text-slate-600">Customer ID:</span>
                        <span className="font-medium text-slate-800">{currentOrder.customerId}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-slate-600">Delivery Address:</span>
                        <span className="font-medium text-slate-800 text-right max-w-xs">{currentOrder.deliveryAddress}</span>
                      </div>
                    </div>
                  </div>

                  <div>
                    <h3 className="text-lg font-semibold text-slate-800 mb-3">Items</h3>
                    <div className="space-y-3">
                      {currentOrder.items.map((item, index) => (
                        <div key={index} className="bg-slate-50 rounded-lg p-4 border border-slate-200">
                          <div className="flex items-start justify-between">
                            <div className="space-y-1">
                              <p className="font-medium text-slate-800">Product Code: {item.productCode}</p>
                              <p className="text-sm text-slate-600">Quantity: {item.quantity}</p>
                              {item.fulfilledByWarehouseId && (
                                <p className="text-sm text-slate-600">Warehouse: {item.fulfilledByWarehouseId}</p>
                              )}
                            </div>
                            {item.itemStatus && (
                              <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getItemStatusColor(item.itemStatus)}`}>
                                {formatStatus(item.itemStatus)}
                              </span>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="pt-4 border-t border-slate-200">
                    <details className="text-sm">
                      <summary className="cursor-pointer font-medium text-slate-700 hover:text-slate-900">
                        View Raw Response
                      </summary>
                      <pre className="mt-3 text-xs text-slate-700 bg-slate-50 p-3 rounded border border-slate-200 overflow-auto">
                        {JSON.stringify(currentOrder, null, 2)}
                      </pre>
                    </details>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
