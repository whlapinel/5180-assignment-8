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

import java.time.Instant;
import java.util.ArrayList;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mBills.clear();
        mBills.addAll(mListener.getAllBills());
        mBills.add(new Bill("Groceries", "Marshmallows", Date.from(Instant.now()), 0.15, 30.50));
        mAdapter = new BillsAdapter(getContext(), mBills);
        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.listView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);


        binding.textViewSortedBy.setText("Sorted By " + sortAttribute + " (" + sortOrder + ")");

        binding.buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clearAllBills();
            }
        });

        binding.buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoCreateBill();
            }
        });

        binding.buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoSortSelection();
            }
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
            binding.billListItemTitle.setText(getItem(position).getName());
            binding.billAmount.setText("Bill Amount: $" + getItem(position).getAmount());
            return view;
        }
    }
}