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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mBills.clear();
        mBills.addAll(mListener.getAllBills());
        mBills.add(new Bill("Groceries", "Milk", new Date(124, 0, 1), 0.10, 25.00));
        mBills.add(new Bill("Entertainment", "Movies", new Date(124, 4, 20), 0.0, 30.00));
        mBills.add(new Bill("Dining", "Restaurant", new Date(124, 3, 10), 0.15, 75.00));
        mBills.add(new Bill("Housing", "Rent", new Date(124, 2, 31), 0.05, 1000.00));
        mBills.add(new Bill("Utilities", "Electricity", new Date(124, 1, 15), 0.0, 50.00));
        mBills = sortBills(mBills, sortAttribute, sortOrder);
        mAdapter = new BillsAdapter(getContext(), mBills);
        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ListView listView = binding.listView;
        listView.setAdapter(mAdapter);
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
            if (convertView != null) {
                binding = BillListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            } else {
                binding = BillListItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
                view = binding.getRoot();
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