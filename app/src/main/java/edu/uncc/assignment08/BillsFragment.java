package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import edu.uncc.assignment08.databinding.BillListItemBinding;
import edu.uncc.assignment08.databinding.FragmentBillsBinding;

public class BillsFragment extends Fragment {
    private static final String TAG = "BillsFragment";
    FragmentBillsBinding binding;

    private ArrayList<Bill> mBills = new ArrayList<>();
    String sortAttribute = "Date", sortOrder = "ASC";
    BillsListener mListener;
    private BillsAdapter mAdapter;

    public BillsFragment() {
        Log.d(TAG, "BillsFragment: ");
        // Required empty public constructor
    }


    public void setSortItems(String sortAttribute, String sortOrder) {
        Log.d(TAG, "setSortItems: ");
        this.sortAttribute = sortAttribute;
        this.sortOrder = sortOrder;
    }

    private ArrayList<Bill> sortBills(ArrayList<Bill> bills, String sortAttribute, String sortOrder) {
        Log.d(TAG, "sortBills: ");
        // Define a comparator based on the sortAttribute and sortOrder
        Comparator<Bill> comparator = new Comparator<Bill>() {
            @Override
            public int compare(Bill bill1, Bill bill2) {
                if (sortAttribute.equalsIgnoreCase("date")) {
                    if (sortOrder.equalsIgnoreCase("asc")) {
                        return bill1.getBillDate().compareTo(bill2.getBillDate());
                    } else {
                        return bill2.getBillDate().compareTo(bill1.getBillDate());
                    }
                } else if (sortAttribute.equalsIgnoreCase("discount")) {
                    if (sortOrder.equalsIgnoreCase("asc")) {
                        return Double.compare(bill1.getDiscount(), bill2.getDiscount());
                    } else {
                        return Double.compare(bill2.getDiscount(), bill1.getDiscount());
                    }
                } else if (sortAttribute.equalsIgnoreCase("category")) {
                    if (sortOrder.equalsIgnoreCase("asc")) {
                        return bill1.getCategory().compareToIgnoreCase(bill2.getCategory());
                    } else {
                        return bill2.getCategory().compareToIgnoreCase(bill1.getCategory());
                    }
                } else {
                    // Handle invalid sortAttribute
                    throw new IllegalArgumentException("Invalid sort attribute: " + sortAttribute);
                }
            }
        };

        // Sort the bills ArrayList using the defined comparator
        bills.sort(comparator);

        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        // TODO: onCreateView runs after this method and wipes all changes. Need to fix.
        Log.d(TAG, "setBills: ");
        for (Bill bill : bills) {
            Log.d(TAG, "setBills: MainActivity bills: " + bill.getName());
        }
        mBills.clear();
        mBills.addAll(bills);
        for (Bill bill : mBills) {
            Log.d(TAG, "setBills: BillsFragment bills: " + bill.getName());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mBills.clear();
        mBills.addAll(mListener.getAllBills());
        mBills = sortBills(mBills, sortAttribute, sortOrder);
        mAdapter = new BillsAdapter(getContext(), mBills);
        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ListView listView = binding.listView;
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bill bill = mBills.get(position);
                mListener.goToBillSummary(bill);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);


        binding.textViewSortedBy.setText("Sorted By " + sortAttribute + " (" + sortOrder + ")");

        binding.buttonClear.setOnClickListener(v -> {
            mListener.clearAllBills();
            mBills.clear();
            mAdapter.notifyDataSetChanged();
        });

        binding.buttonNew.setOnClickListener(v -> {
            mListener.gotoCreateBill();
        });

        binding.buttonSort.setOnClickListener(v -> {
            mListener.gotoSortSelection();
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
        if (context instanceof BillsListener) {
            mListener = (BillsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BillsListener");
        }
    }

    interface BillsListener {
        void goToBillSummary(Bill bill);

        ArrayList<Bill> getAllBills();

        void gotoCreateBill();

        void gotoSortSelection();

        void clearAllBills();
    }

    public static class BillsAdapter extends ArrayAdapter<Bill> {
        private static final String TAG = "BillsAdapter";

        public BillsAdapter(@NonNull Context context, List<Bill> bills) {
            super(context, 0, bills);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Log.d(TAG, "getView: ");
            View view = convertView;
            BillListItemBinding binding;
            if (convertView == null) {
                binding = BillListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
                view = binding.getRoot();
                view.setTag(binding);
            } else {
                binding = (BillListItemBinding) view.getTag();
            }
            Double discount = getItem(position).getDiscount();
            Double amount = getItem(position).getAmount();
            String amountString = String.format("%.2f", amount);
            Double total = amount - discount * amount;
            String totalString = String.format("%.2f", total);
            String dollarDiscount = String.format("%.2f", discount * amount);
            String discountPcString = String.format("%.0f", discount * 100);
            String discountString = (discountPcString + "%" + " ($" + dollarDiscount + ")");
            binding.billListItemTitle.setText(getItem(position).getName());
            binding.billAmount.setText("Bill Amount: $" + amountString);
            binding.billDiscount.setText("Discount: " + discountString);
            binding.billTotal.setText("Total: $" + totalString);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            binding.billDate.setText("Bill Date: " + sdf.format(getItem(position).getBillDate()));
            binding.billCategory.setText("Category: " + getItem(position).getCategory());
            return view;
        }
    }
}